package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Booking
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.UserRole
import com.example.data.repository.FixoRepository
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.screens.customer.BookingPackageOption
import com.example.ui.screens.worker.PhotoInspectionValidator
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
 * CHANTIER 6 & 7 : TESTS DU WORKROOM EN DIRECT, SÉQUESTRE ET CONTRAT ANTI-CASH
 *
 * Valide :
 * 1. Le verrouillage des fonds sous séquestre lors du checkout (15 000 FCFA Flash / 12 000 FCFA Standard).
 * 2. La progression d'états : DISPATCHED ➔ ARTISAN_EN_ROUTE ➔ ON_SITE ➔ WORK_IN_PROGRESS ➔ COMPLETED_PENDING_HANDSHAKE.
 * 3. L'obligation stricte de la photo d'état initial avant de débloquer le chronomètre de travail.
 * 4. L'impossibilité stricte de générer la clôture / QR Code sans la photo d'état final.
 * 5. La présence et la rigueur du contrat Zero-Cash et de la localisation bilingue (FR / EN).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Chantier6And7LiveWorkroomTest {

    private lateinit var context: Context
    private lateinit var repository: FixoRepository

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        repository = FixoRepository(context)
        repository.loadSandboxTestData()
    }

    @Test
    fun testEscrowCheckoutAndZeroCashLocking() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()

        assertNotNull("Worker should be present in sandbox", workers.firstOrNull())
        assertNotNull("Customer should be present in sandbox", customer)

        val targetWorker = workers.first()
        val flashPackage = BookingPackageOption.FLASH
        assertEquals(15000.0, flashPackage.price, 0.0)

        val service = ServiceItem(
            id = "srv_leak_fix",
            workerId = targetWorker.id,
            name = "Plomberie sanitaire (Fuite d'eau standard)",
            category = ServiceCategory.PLUMBING,
            description = "Réparation d'urgence sous évier",
            price = flashPackage.price
        )

        // Création du booking avec séquestre
        val booking = repository.createBooking(
            customerId = customer!!.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat (< 30 min)",
            address = "Akwa, Rue Drouot",
            notes = "Fuite tuyau cuivre sous évier",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // Mise à jour de l'état vers DISPATCHED avec formule Flash
        val dispatched = booking.copy(
            status = JobStatus.DISPATCHED,
            packageTier = flashPackage.name
        )
        repository.dao.updateBooking(dispatched)

        val stored = repository.dao.getBookingByIdDirect(booking.id)
        assertNotNull(stored)
        assertEquals(JobStatus.DISPATCHED, stored!!.status)
        assertEquals(EscrowStatus.HOLDING, stored.escrowStatus)
        assertEquals(15000.0, stored.priceAmount, 0.0)
        assertEquals("FLASH", stored.packageTier)

        // Vérification de la formule Standard (12 000 FCFA)
        val standardPackage = BookingPackageOption.STANDARD
        assertEquals(12000.0, standardPackage.price, 0.0)
    }

    @Test
    fun testFullStateProgressionLifecycle() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_cycle_test",
            workerId = targetWorker.id,
            name = "Dépannage Plomberie",
            category = ServiceCategory.PLUMBING,
            description = "Fuite d'eau",
            price = 15000.0
        )

        val initialBooking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = targetWorker,
            service = service,
            date = "Aujourd'hui",
            timeSlot = "Immédiat",
            address = "Rue Drouot, Akwa",
            notes = "Test cycle complet",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // 1. ÉTAT 1 : DISPATCHED (Après verrouillage checkout séquestre)
        val dispatchedBooking = initialBooking.copy(status = JobStatus.DISPATCHED)
        repository.dao.updateBooking(dispatchedBooking)
        var current = repository.dao.getBookingByIdDirect(initialBooking.id)
        assertEquals(JobStatus.DISPATCHED, current?.status)

        // 2. ÉTAT 2 : ARTISAN_EN_ROUTE (Après Slide to Accept par l'artisan)
        repository.startWorkerTrip(initialBooking.id, 4.0483, 9.7043, JobStatus.ARTISAN_EN_ROUTE)
        current = repository.dao.getBookingByIdDirect(initialBooking.id)
        assertEquals(JobStatus.ARTISAN_EN_ROUTE, current?.status)
        assertTrue(current?.trackingActive == true)

        // 3. ÉTAT 3 : ON_SITE (L'artisan clique sur "Je suis arrivé sur place")
        repository.markWorkerArrived(initialBooking.id, JobStatus.ON_SITE)
        current = repository.dao.getBookingByIdDirect(initialBooking.id)
        assertEquals(JobStatus.ON_SITE, current?.status)
        assertFalse(current?.trackingActive == true)

        // 4. ÉTAT 4 : WORK_IN_PROGRESS (Photo avant soumise -> Démarrage des travaux et chronomètre)
        repository.submitInitialPhoto(
            initialBooking.id,
            "https://fixo.cm/photos/before_test.jpg",
            4.0505,
            9.6950
        )
        val started = repository.startWork(initialBooking.id, requireBeforePhoto = true, statusToSet = JobStatus.WORK_IN_PROGRESS)
        assertTrue("Start work must succeed once initial photo is present", started)
        current = repository.dao.getBookingByIdDirect(initialBooking.id)
        assertEquals(JobStatus.WORK_IN_PROGRESS, current?.status)
        assertNotNull(current?.beforePhotoUrl)

        // 5. ÉTAT 5 : COMPLETED_PENDING_HANDSHAKE (Photo finale soumise -> Génération QR Code)
        repository.submitFinalPhoto(
            initialBooking.id,
            "https://fixo.cm/photos/after_test.jpg",
            4.0505,
            9.6950
        )
        val completed = repository.requestJobCompletion(initialBooking.id, requireAfterPhoto = true, statusToSet = JobStatus.COMPLETED_PENDING_HANDSHAKE)
        assertTrue("Request completion must succeed once final photo is present", completed)
        current = repository.dao.getBookingByIdDirect(initialBooking.id)
        assertEquals(JobStatus.COMPLETED_PENDING_HANDSHAKE, current?.status)
        assertNotNull(current?.afterPhotoUrl)
    }

    @Test
    fun testMandatoryInitialPhotoBeforeWorkBlocker() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_photo_test",
            workerId = targetWorker.id,
            name = "Fuite tuyauterie",
            category = ServiceCategory.PLUMBING,
            description = "Test photo initiale",
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
            notes = "Test",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // Arrivée sur site sans photo
        val onSiteBooking = booking.copy(status = JobStatus.ON_SITE, beforePhotoUrl = null)
        repository.dao.updateBooking(onSiteBooking)

        // 1. Validateur métier : avant photo -> faux
        assertFalse(PhotoInspectionValidator.canStartWork(null))
        assertFalse(PhotoInspectionValidator.canStartWork(""))

        // 2. Tentative de démarrer les travaux sans photo -> Rejeté
        val startedWithoutPhoto = repository.startWork(booking.id, requireBeforePhoto = true)
        assertFalse("Cannot start work without mandatory before-work photo", startedWithoutPhoto)

        var stored = repository.dao.getBookingByIdDirect(booking.id)
        assertEquals(JobStatus.ON_SITE, stored?.status)

        // 3. Prise de la photo initiale
        repository.submitInitialPhoto(booking.id, "https://fixo.cm/photos/pipe_burst.jpg")
        stored = repository.dao.getBookingByIdDirect(booking.id)
        assertNotNull(stored?.beforePhotoUrl)
        assertTrue(PhotoInspectionValidator.canStartWork(stored?.beforePhotoUrl))

        // 4. Tentative de démarrer avec photo -> Succès
        val startedWithPhoto = repository.startWork(booking.id, requireBeforePhoto = true)
        assertTrue("Work and stopwatch unlock successfully with photo", startedWithPhoto)

        stored = repository.dao.getBookingByIdDirect(booking.id)
        assertEquals(JobStatus.WORK_IN_PROGRESS, stored?.status)
    }

    @Test
    fun testMandatoryFinalPhotoBeforeQrInvoiceBlocker() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_after_photo_test",
            workerId = targetWorker.id,
            name = "Test Photo Finale",
            category = ServiceCategory.PLUMBING,
            description = "Test après travaux",
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
            notes = "Test",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // Travaux en cours sans photo finale
        val workingBooking = booking.copy(
            status = JobStatus.WORK_IN_PROGRESS,
            beforePhotoUrl = "https://fixo.cm/photos/before.jpg",
            afterPhotoUrl = null
        )
        repository.dao.updateBooking(workingBooking)

        // 1. Validateur métier : après photo -> faux
        assertFalse(PhotoInspectionValidator.canGenerateInvoiceQr(null))
        assertFalse(PhotoInspectionValidator.canGenerateInvoiceQr(""))

        // 2. Tentative de clôturer sans photo finale -> Rejeté
        val completedWithoutPhoto = repository.requestJobCompletion(booking.id, requireAfterPhoto = true)
        assertFalse("Cannot complete job without mandatory after-repair photo", completedWithoutPhoto)

        var stored = repository.dao.getBookingByIdDirect(booking.id)
        assertEquals(JobStatus.WORK_IN_PROGRESS, stored?.status)

        // 3. Prise de la photo finale
        repository.submitFinalPhoto(booking.id, "https://fixo.cm/photos/after.jpg")
        stored = repository.dao.getBookingByIdDirect(booking.id)
        assertNotNull(stored?.afterPhotoUrl)
        assertTrue(PhotoInspectionValidator.canGenerateInvoiceQr(stored?.afterPhotoUrl))

        // 4. Tentative de clôturer avec photo -> Succès et passage en COMPLETED_PENDING_HANDSHAKE
        val completedWithPhoto = repository.requestJobCompletion(booking.id, requireAfterPhoto = true)
        assertTrue("Job completion unlocks successfully with final photo", completedWithPhoto)

        stored = repository.dao.getBookingByIdDirect(booking.id)
        assertEquals(JobStatus.COMPLETED_PENDING_HANDSHAKE, stored?.status)
    }

    @Test
    fun testSandboxSimulationWorkflow() = runBlocking {
        val workers = repository.getAllWorkers().first()
        val customer = repository.getUserById("usr_cust_1").first()!!
        val targetWorker = workers.first()

        val service = ServiceItem(
            id = "srv_sim_test",
            workerId = targetWorker.id,
            name = "Test Simulation",
            category = ServiceCategory.PLUMBING,
            description = "Simulation instantanée",
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
            notes = "Simulation test",
            paymentMethod = PaymentMethod.MTN_MOMO
        )

        // Exécution de la simulation rapide
        repository.simulateArrivalAndPhotos(booking.id)

        val stored = repository.dao.getBookingByIdDirect(booking.id)
        assertNotNull(stored)
        // Vérifie que les deux photos ont été certifiées
        assertNotNull("Initial photo should be populated by simulation", stored?.beforePhotoUrl)
        assertNotNull("Final photo should be populated by simulation", stored?.afterPhotoUrl)
        // Les travaux sont prêts pour le QR code
        assertTrue(PhotoInspectionValidator.canGenerateInvoiceQr(stored?.afterPhotoUrl))
    }

    @Test
    fun testZeroCashAndWorkroomI18nStrings() {
        val warningFr = FixoStrings.get("workroom.anti_cash_warning", AppLanguage.FR)
        assertTrue(warningFr.contains("FIXO"))
        assertTrue(warningFr.contains("espèces"))

        val warningEn = FixoStrings.get("workroom.anti_cash_warning", AppLanguage.EN)
        assertTrue(warningEn.contains("FIXO"))

        val voiceNoteFr = FixoStrings.get("workroom.chat.voice_note_hold", AppLanguage.FR)
        assertTrue(voiceNoteFr.contains("vocale") || voiceNoteFr.contains("Maintenir"))

        val maskedCallFr = FixoStrings.get("workroom.chat.masked_call_label", AppLanguage.FR)
        assertTrue(maskedCallFr.contains("VoIP") || maskedCallFr.contains("audio"))
    }
}
