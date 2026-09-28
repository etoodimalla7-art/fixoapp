package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Booking
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.LoyaltyTier
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.isClosed
import com.example.data.model.isDisputed
import com.example.data.repository.FixoRepository
import com.example.data.rewards.RewardsModule
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * CHANTIERS 8, 9 & 10 : TESTS CLÔTURE PHYSIQUE QR/PIN, FINANCE MOBILE MONEY, RÉCOMPENSES & LITIGES / GARANTIE
 *
 * Valide :
 * 1. Le décodage du QR Code dynamique et la correspondance du Code PIN de secours à 4 chiffres.
 * 2. La transaction atomique de clôture :
 *    - Libération du séquestre vers le solde disponible de l'artisan (90% net = 13 500 FCFA sur 15 000 FCFA).
 *    - Déduction de la commission FIXO (10% = 1 500 FCFA).
 *    - Débit du séquestre client et attribution de 5% en points de fidélité (750 Points).
 * 3. Le gel immédiat des fonds lors du déclenchement d'un litige (DISPUTE_FROZEN & EscrowStatus.DISPUTED).
 * 4. La simulation de décaissement (Cash-Out) instantané vers MTN Mobile Money et Orange Money Cameroun.
 * 5. La couverture du programme de garantie 14 jours Fixo Shield.
 * 6. Le moteur de fidélité Rewards (Paliers Bronze, Silver, Gold et déduction au checkout).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Chantier8To10CompletionFinanceTest {

    private lateinit var context: Context
    private lateinit var repository: FixoRepository

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        repository = FixoRepository(context)
        repository.loadSandboxTestData()
    }

    @Test
    fun testPhysicalHandshakeClosingWithQrPayload() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_leak_closing",
            workerId = targetWorker.id,
            name = "Plomberie sanitaire (Fuite d'eau standard)",
            category = ServiceCategory.PLUMBING,
            description = "Réparation d'urgence sous évier",
            price = 15000.0
        )

        val booking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat",
            address = "Akwa, Douala",
            notes = "Test handshake QR",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // Simuler le passage en phase finale après photos obligatoires
        repository.simulateArrivalAndPhotos(booking.id)
        val readyBooking = repository.dao.getBookingByIdDirect(booking.id)!!
        assertEquals(15000.0, readyBooking.priceAmount, 0.0)

        val workerUserBefore = repository.dao.getUserById(targetWorker.userId).first()!!
        val customerUserBefore = repository.dao.getUserById(customer.id).first()!!
        val initialWorkerBalance = workerUserBefore.balance
        val initialCustomerPoints = customerUserBefore.fixoPoints

        // 1. Décodage et validation via payload QR Code dynamique
        val qrPayload = "fixo://handshake?jobId=${readyBooking.id}&amount=15000&pin=${readyBooking.handshakePin}&sig=FIXO_SECURE_AUTH"
        val handshakeSuccess = repository.completeJobWithHandshake(readyBooking.id, qrPayload)
        assertTrue("Handshake with dynamic QR payload must succeed", handshakeSuccess)

        // 2. Vérification des états du chantier
        val closedBooking = repository.dao.getBookingByIdDirect(readyBooking.id)!!
        assertEquals(JobStatus.CLOSED_CONFIRMED, closedBooking.status)
        assertTrue(closedBooking.status.isClosed)
        assertEquals(EscrowStatus.RELEASED, closedBooking.escrowStatus)
        assertNotNull(closedBooking.completionTimestamp)
        assertNotNull(closedBooking.warrantyExpiryTimestamp)

        // 3. Vérification de la transaction atomique (Gain net 13 500 FCFA vers solde disponible)
        val workerUserAfter = repository.dao.getUserById(targetWorker.userId).first()!!
        val expectedNetGain = 15000.0 * 0.90 // 13 500 FCFA
        assertEquals(initialWorkerBalance + expectedNetGain, workerUserAfter.balance, 0.01)

        // 4. Vérification de l'attribution des 5% de points de fidélité (750 Points pour 15 000 FCFA)
        val customerUserAfter = repository.dao.getUserById(customer.id).first()!!
        val expectedPointsEarned = (15000.0 * 0.05).toInt() // 750
        assertEquals(initialCustomerPoints + expectedPointsEarned, customerUserAfter.fixoPoints)
        assertEquals(750, expectedPointsEarned)
    }

    @Test
    fun testPhysicalHandshakeClosingWithFallbackPin() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_pin_test",
            workerId = targetWorker.id,
            name = "Dépannage Électrique",
            category = ServiceCategory.ELECTRICAL,
            description = "Disjoncteur",
            price = 15000.0
        )

        val booking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat",
            address = "Bonanjo",
            notes = "Test fallback PIN",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        repository.simulateArrivalAndPhotos(booking.id)
        val readyBooking = repository.dao.getBookingByIdDirect(booking.id)!!
        val correctPin = readyBooking.handshakePin

        // 1. PIN erroné -> Rejeté
        val wrongPinSuccess = repository.completeJobWithHandshake(readyBooking.id, "0000")
        assertFalse("Wrong PIN must be rejected", wrongPinSuccess)

        // 2. PIN exact à 4 chiffres -> Validé
        val rightPinSuccess = repository.completeJobWithHandshake(readyBooking.id, correctPin)
        assertTrue("Correct fallback PIN must unlock payment", rightPinSuccess)

        val completed = repository.dao.getBookingByIdDirect(readyBooking.id)!!
        assertEquals(JobStatus.CLOSED_CONFIRMED, completed.status)
        assertEquals(EscrowStatus.RELEASED, completed.escrowStatus)
    }

    @Test
    fun testDisputeImmediateEscrowFreeze() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_dispute_test",
            workerId = targetWorker.id,
            name = "Plomberie sanitaire",
            category = ServiceCategory.PLUMBING,
            description = "Fuite",
            price = 15000.0
        )

        val booking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat",
            address = "Akwa",
            notes = "Test litige",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        repository.simulateArrivalAndPhotos(booking.id)

        // Déclenchement du litige avec photos contradictoires
        val disputeReason = "Fuite persistante après réparation"
        val contestationPhotos = listOf("https://fixo.cm/claim1.jpg", "https://fixo.cm/claim2.jpg", "https://fixo.cm/claim3.jpg")
        val freezeSuccess = repository.freezeDispute(
            bookingId = booking.id,
            reason = disputeReason,
            photos = contestationPhotos,
            notes = "L'eau coule toujours sous l'évier malgré le remplacement du joint."
        )
        assertTrue("Freeze dispute must succeed", freezeSuccess)

        val frozenBooking = repository.dao.getBookingByIdDirect(booking.id)!!
        assertEquals(JobStatus.DISPUTE_FROZEN, frozenBooking.status)
        assertTrue(frozenBooking.status.isDisputed)
        assertEquals(EscrowStatus.DISPUTED, frozenBooking.escrowStatus)
        assertEquals(disputeReason, frozenBooking.disputeReason)
        assertEquals(3, frozenBooking.disputePhotosJson?.split(",")?.size)

        // Les litiges sont consultables dans les rapports de litige
        val disputes = repository.getAllDisputes().first()
        val relatedDispute = disputes.find { it.reportedId == booking.id }
        assertNotNull(relatedDispute)
        assertEquals(disputeReason, relatedDispute?.reason)
    }

    @Test
    fun testInstantCashOutToMtnAndOrangeMoney() = runBlocking {
        val workerUser = repository.dao.getUserById("usr_worker_1").first()!!
        // Configurer un solde disponible initial
        repository.dao.updateUser(workerUser.copy(balance = 48500.0))

        var currentWorker = repository.dao.getUserById("usr_worker_1").first()!!
        assertEquals(48500.0, currentWorker.balance, 0.0)

        // 1. Décaissement vers MTN Mobile Money (40 000 FCFA)
        val mtnResult = repository.processCashOut(
            userId = currentWorker.id,
            amount = 40000.0,
            operator = CameroonMobileOperator.MTN_MOMO,
            phoneNumber = "+237 671 234 567"
        )
        assertTrue("MTN MoMo cash-out must succeed", mtnResult.isSuccess)

        currentWorker = repository.dao.getUserById("usr_worker_1").first()!!
        assertEquals(8500.0, currentWorker.balance, 0.0)

        // 2. Décaissement vers Orange Money (5 000 FCFA)
        val orangeResult = repository.processCashOut(
            userId = currentWorker.id,
            amount = 5000.0,
            operator = CameroonMobileOperator.ORANGE_MONEY,
            phoneNumber = "+237 699 887 766"
        )
        assertTrue("Orange Money cash-out must succeed", orangeResult.isSuccess)

        currentWorker = repository.dao.getUserById("usr_worker_1").first()!!
        assertEquals(3500.0, currentWorker.balance, 0.0)

        // 3. Tentative de retrait d'un montant supérieur au solde -> Échec
        val overdraftResult = repository.processCashOut(
            userId = currentWorker.id,
            amount = 10000.0,
            operator = CameroonMobileOperator.MTN_MOMO,
            phoneNumber = "+237 671 234 567"
        )
        assertTrue("Overdraft cash-out must fail", overdraftResult.isFailure)
        assertEquals(3500.0, currentWorker.balance, 0.0)
    }

    @Test
    fun testFixo14DayWarrantyCoverage() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_warr_test",
            workerId = targetWorker.id,
            name = "Plomberie sanitaire",
            category = ServiceCategory.PLUMBING,
            description = "Fuite",
            price = 15000.0
        )

        val booking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat",
            address = "Akwa",
            notes = "Test garantie",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        repository.simulateArrivalAndPhotos(booking.id)
        repository.completeJobWithHandshake(booking.id, booking.handshakePin)

        // Déclaration sous garantie 14 jours
        val claimSuccess = repository.claimWarranty(
            bookingId = booking.id,
            issueDescription = "Légère récidive du suintement d'eau sur le tuyau réparé.",
            photos = listOf("https://fixo.cm/relapse.jpg")
        )
        assertTrue("Warranty claim within 14 days must succeed", claimSuccess)

        val disputes = repository.getAllDisputes().first()
        val warrantyReport = disputes.find { it.reportedId == booking.id && it.reportedType == "WARRANTY_CLAIM" }
        assertNotNull(warrantyReport)
    }

    @Test
    fun testRewardsModuleRulesAndTiers() {
        // 5% Cashback
        assertEquals(750, RewardsModule.calculatePointsEarned(15000.0))
        assertEquals(600, RewardsModule.calculatePointsEarned(12000.0))

        // Tiers
        assertEquals(LoyaltyTier.BRONZE, RewardsModule.getTierForPoints(100))
        assertEquals(LoyaltyTier.SILVER, RewardsModule.getTierForPoints(350))
        assertEquals(LoyaltyTier.GOLD, RewardsModule.getTierForPoints(800))
        assertEquals(LoyaltyTier.PLATINUM, RewardsModule.getTierForPoints(2000))

        // Conversion 1 Point = 1 FCFA
        assertEquals(350.0, RewardsModule.calculateDiscountFromPoints(350), 0.0)

        // Déduction au checkout
        assertEquals(350, RewardsModule.calculateMaxUsablePoints(350, 15000.0))
    }

    @Test
    fun testChantiers8To10I18nStrings() {
        val handshakeTitleFr = FixoStrings.get("handshake.title", AppLanguage.FR)
        assertTrue(handshakeTitleFr.contains("CLÔTURE"))

        val fallbackPinFr = FixoStrings.get("handshake.fallback_pin_label", AppLanguage.FR)
        assertTrue(fallbackPinFr.contains("PIN"))

        val momoCashoutFr = FixoStrings.get("wallet.cashout_cta", AppLanguage.FR)
        assertTrue(momoCashoutFr.contains("MOMO") || momoCashoutFr.contains("FONDS"))

        val disputeTitleFr = FixoStrings.get("dispute.title", AppLanguage.FR)
        assertTrue(disputeTitleFr.contains("Médiation") || disputeTitleFr.contains("Litige"))

        val warrantyBadgeFr = FixoStrings.get("warranty.active_badge", AppLanguage.FR)
        assertTrue(warrantyBadgeFr.contains("Garantie"))
    }
}
