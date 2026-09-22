package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Booking
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceCategory
import com.example.data.model.UserRole
import com.example.data.model.formatFixoCurrency
import com.example.data.repository.FixoRepository
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FixoCoreLifecycleTest {

    private lateinit var context: Context
    private lateinit var repository: FixoRepository

    @Before
    fun setup() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        repository = FixoRepository(context)
        repository.loadSandboxTestData()
    }

    @Test
    fun testCurrencyFormattingIsAlwaysXAFAndNoDollarSign() {
        val formattedZero = formatFixoCurrency(0.0)
        val formatted15000 = formatFixoCurrency(15000.0)
        val formatted50000 = formatFixoCurrency(50000.0)

        assertFalse(formattedZero.contains("$"))
        assertFalse(formatted15000.contains("$"))
        assertFalse(formatted50000.contains("$"))

        assertTrue(formattedZero.contains("FCFA"))
        assertTrue(formatted15000.contains("FCFA"))
        assertTrue(formatted50000.contains("FCFA"))
    }

    @Test
    fun testHaversineDistanceAndEtaCalculation() {
        // Douala Akwa (4.0505, 9.6950) to Bonapriso (4.0150, 9.6920) is ~4 km
        val distance = repository.calculateHaversineDistanceKm(4.0505, 9.6950, 4.0150, 9.6920)
        assertTrue("Distance should be around 3.9 - 4.2 km", distance in 3.5..4.5)

        // At 25 km/h, 4 km takes ~10-12 minutes
        val eta = repository.calculateEtaMinutes(distance, 25.0)
        assertTrue("ETA should be between 8 and 15 minutes", eta in 8..15)
    }

    @Test
    fun testFullBookingLifecycleAndEscrowRelease() = runBlocking {
        val bookings = repository.getAllBookings().first()
        val targetBooking = bookings.firstOrNull()
        assertNotNull("Sandbox should have pre-seeded bookings", targetBooking)
        val bookingId = targetBooking!!.id

        // 1. Worker starts trip
        repository.startWorkerTrip(bookingId, 4.0400, 9.6900)
        var currentBooking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.ON_THE_WAY, currentBooking?.status)
        assertTrue(currentBooking?.trackingActive == true)

        // 2. Worker updates location
        repository.updateWorkerLocation(bookingId, 4.0300, 9.6910, 30f, 180f)
        val workerLoc = repository.getWorkerLocation(bookingId).first()
        assertNotNull(workerLoc)
        assertEquals(4.0300, workerLoc!!.latitude, 0.0001)

        // 3. Worker arrives at site
        repository.markWorkerArrived(bookingId)
        currentBooking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.ARRIVED, currentBooking?.status)
        assertFalse(currentBooking?.trackingActive == true)

        // 4. Worker starts work
        repository.startWork(bookingId)
        currentBooking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.IN_PROGRESS, currentBooking?.status)

        // 5. Worker requests completion
        repository.requestJobCompletion(bookingId)
        currentBooking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.COMPLETION_REQUESTED, currentBooking?.status)

        // 6. Customer approves inspection and releases escrow
        repository.releaseEscrowAndReview(bookingId, 5.0f, "Exceptional craftsmanship!")
        currentBooking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.COMPLETED, currentBooking?.status)
        assertEquals(EscrowStatus.RELEASED, currentBooking?.escrowStatus)
        assertEquals(5.0f, currentBooking?.customerRating)
    }

    @Test
    fun testJobCancellationRefundsEscrowToCustomer() = runBlocking {
        // Deposit sufficient funds first
        repository.depositFunds("usr_cust_1", 50000.0, "MTN_MOMO", "+237671234567")

        val customer = repository.getUserById("usr_cust_1").first()
        assertNotNull(customer)
        val initialBalance = customer!!.balance

        val worker = repository.getWorkerById("wrk_1")
        assertNotNull(worker)

        val service = repository.getServicesForWorker("wrk_1").first().first()

        // Create booking with escrow
        val newBooking = repository.createBooking(
            customerId = customer.id,
            customerName = customer.name,
            worker = worker!!,
            service = service,
            date = "Tomorrow",
            timeSlot = "14:00 - 16:00",
            address = "Rue Deido, Douala",
            notes = "Bathroom leak inspection",
            paymentMethod = PaymentMethod.FIXO_WALLET
        )

        val booked = repository.getBookingById(newBooking.id).first()
        assertEquals(EscrowStatus.HOLDING, booked?.escrowStatus)

        // Cancel booking
        repository.updateJobStatus(newBooking.id, JobStatus.CANCELLED)

        val cancelledBooking = repository.getBookingById(newBooking.id).first()
        assertEquals(JobStatus.CANCELLED, cancelledBooking?.status)
        assertEquals(EscrowStatus.REFUNDED, cancelledBooking?.escrowStatus)

        val refreshedCustomer = repository.getUserById("usr_cust_1").first()
        assertEquals(initialBalance, refreshedCustomer!!.balance, 0.01)
    }

    @Test
    fun testDisputeFilingAndResolution() = runBlocking {
        val disputesBefore = repository.getAllDisputes().first().size
        repository.fileDispute(
            reporterId = "usr_cust_1",
            reporterName = "Jean-Paul Mbarga",
            reportedType = "BOOKING",
            reportedId = "bk_test_123",
            reason = "Artisan requested extra cash outside escrow"
        )

        val disputesAfter = repository.getAllDisputes().first()
        assertEquals(disputesBefore + 1, disputesAfter.size)

        val target = disputesAfter.find { it.reportedId == "bk_test_123" }
        assertNotNull(target)
        assertEquals("PENDING", target!!.status)

        // Resolve dispute
        repository.resolveDispute(target.id, "Warned artisan regarding escrow terms; resolved with client.")
        val resolvedDisputes = repository.getAllDisputes().first()
        val resolvedTarget = resolvedDisputes.find { it.id == target.id }
        assertEquals("RESOLVED", resolvedTarget?.status)
    }

    @Test
    fun testChatMessagingBetweenCustomerAndWorker() = runBlocking {
        val bookingId = "bk_1"
        val messageCountBefore = repository.getMessagesForBooking(bookingId).first().size

        repository.sendMessage(
            bookingId = bookingId,
            senderId = "usr_cust_1",
            senderName = "Jean-Paul Mbarga",
            senderRole = UserRole.CUSTOMER,
            message = "Hello! Please bring 3/4 inch brass fittings."
        )

        val messagesAfter = repository.getMessagesForBooking(bookingId).first()
        assertEquals(messageCountBefore + 1, messagesAfter.size)
        val lastMsg = messagesAfter.last()
        assertEquals("Hello! Please bring 3/4 inch brass fittings.", lastMsg.message)
        assertEquals("DELIVERED", lastMsg.deliveryState)
    }

    @Test
    fun testWorkforceRecruitmentListingAndApplication() = runBlocking {
        val requestsBefore = repository.getAllWorkforceRequests().first().size
        val newRequest = com.example.data.model.WorkforceRequest(
            id = "wfr_test_1",
            organizationId = "org_1",
            organizationName = "Société Camerounaise de BTP",
            projectTitle = "Douala Port Expansion Electrical Wiring",
            category = ServiceCategory.ELECTRICAL,
            requiredCount = 5,
            recruitedCount = 0,
            ratePerDayXaf = 25000.0,
            location = "Douala Port, Littoral",
            startDate = "2026-10-01",
            endDate = "2026-10-15",
            description = "Industrial 3-phase wiring and panel installations."
        )
        repository.createWorkforceRequest(newRequest)

        val requestsAfter = repository.getAllWorkforceRequests().first()
        assertEquals(requestsBefore + 1, requestsAfter.size)

        val created = requestsAfter.find { it.id == "wfr_test_1" }
        assertNotNull(created)
        assertEquals(0, created!!.recruitedCount)
        assertEquals(25000.0, created.dailyRate, 0.01)

        // Artisan applies
        repository.applyForWorkforceRequest("wfr_test_1", "wrk_1")
        val updated = repository.getAllWorkforceRequests().first().find { it.id == "wfr_test_1" }
        assertEquals(1, updated?.recruitedCount)

        // Check enterprise notification was generated
        val notifs = repository.getNotificationsForUser("org_1").first()
        assertTrue("Enterprise should receive workforce application notification", notifs.any { it.type == "WORKFORCE_APPLICATION" })
    }

    @Test
    fun testWorkerLiveTrackingGpsUpdatesAndCleanupOnArrival() = runBlocking {
        val bookings = repository.getAllBookings().first()
        val targetBooking = bookings.first()
        val bookingId = targetBooking.id

        // Start trip
        repository.startWorkerTrip(bookingId, 4.0505, 9.6950)
        var booking = repository.getBookingById(bookingId).first()
        assertTrue(booking?.trackingActive == true)

        // Update GPS
        repository.updateWorkerLocation(bookingId, 4.0450, 9.6930, 22.5f, 195.0f)
        var location = repository.getWorkerLocation(bookingId).first()
        assertNotNull(location)
        assertEquals(4.0450, location!!.latitude, 0.0001)
        assertEquals(9.6930, location.longitude, 0.0001)
        assertEquals(22.5f, location.speedKmh, 0.1f)
        assertEquals(195.0f, location.heading, 0.1f)

        // Mark arrived
        repository.markWorkerArrived(bookingId)
        booking = repository.getBookingById(bookingId).first()
        assertEquals(JobStatus.ARRIVED, booking?.status)
        assertFalse(booking?.trackingActive == true)
    }

    @Test
    fun testVerificationStatusTransitions() = runBlocking {
        val user = repository.getUserById("usr_worker_1").first()
        assertNotNull(user)

        val pendingUser = user!!.copy(verificationStatus = com.example.data.model.VerificationStatus.PENDING)
        repository.updateCurrentUser(pendingUser)

        val updated = repository.getUserById("usr_worker_1").first()
        assertEquals(com.example.data.model.VerificationStatus.PENDING, updated?.verificationStatus)
    }
}
