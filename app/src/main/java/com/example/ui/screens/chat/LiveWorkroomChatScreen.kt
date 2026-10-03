package com.example.ui.screens.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.ui.FixoViewModel
import com.example.ui.theme.FixoGold500

/**
 * LiveWorkroomChatScreen: Screen wrapper for Compose Navigation routing.
 * Binds the workerId to active chat sessions in FixoViewModel and displays LiveWorkroomChat.
 */
@Composable
fun LiveWorkroomChatScreen(
    workerId: String,
    onBackClick: () -> Unit,
    viewModel: FixoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var targetBooking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(workerId) {
        val cleanWorkerId = workerId.trim()
        val worker = uiState.allWorkers.find {
            it.id.equals(cleanWorkerId, ignoreCase = true) ||
                    it.name.lowercase().contains(cleanWorkerId.lowercase().replace("_", " "))
        } ?: uiState.allWorkers.firstOrNull { it.id == "wrk_1" } ?: uiState.allWorkers.firstOrNull()

        if (worker != null) {
            viewModel.startChatWithWorker(worker) { bookingId ->
                val found = uiState.allBookings.find { it.id == bookingId }
                    ?: uiState.customerBookings.find { it.id == bookingId }
                    ?: uiState.workerBookings.find { it.id == bookingId }
                    ?: uiState.selectedBooking
                targetBooking = found
                isLoading = false
            }
        } else {
            targetBooking = uiState.selectedBooking ?: uiState.customerBookings.firstOrNull()
            isLoading = false
        }
    }

    val booking = targetBooking ?: uiState.selectedBooking ?: uiState.customerBookings.firstOrNull()

    if (booking != null) {
        val messages = uiState.chatMessages.ifEmpty {
            uiState.allMessages.filter { it.bookingId == booking.id }
        }

        LiveWorkroomChat(
            booking = booking,
            messages = messages,
            currentUserRole = uiState.currentRole,
            currentUserId = uiState.currentUser?.id ?: "usr_cust_1",
            language = uiState.currentLanguage,
            onBack = onBackClick,
            onSendMessage = { text, attachmentUrl, attachmentType, durationSec ->
                viewModel.sendWorkroomChatMessage(
                    booking.id,
                    text,
                    attachmentUrl,
                    attachmentType,
                    durationSec
                )
            },
            modifier = modifier
        )
    } else if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = FixoGold500)
        }
    } else {
        // Fallback stub booking for preview / testing
        val fallbackBooking = Booking(
            id = "bk_chat_$workerId",
            customerId = uiState.currentUser?.id ?: "usr_cust_1",
            customerName = uiState.currentUser?.name ?: "Sarah Jenkins",
            workerId = workerId,
            workerName = "Marc Dubois",
            workerAvatar = "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=400",
            serviceTitle = "Devis Forfaitaire Akwa (15 000 FCFA)",
            category = com.example.data.model.ServiceCategory.PLUMBING,
            date = "Aujourd'hui",
            timeSlot = "14:30",
            status = JobStatus.ACCEPTED,
            address = "Rue Drouot, Akwa, Douala",
            notes = "Demande d'information devis forfaitaire 15 000 FCFA",
            priceAmount = 15000.0,
            escrowStatus = com.example.data.model.EscrowStatus.HOLDING,
            paymentMethod = com.example.data.model.PaymentMethod.ORANGE_MONEY,
            workerPhone = "+237 690 445 566",
            customerPhone = "+237 670 112 233"
        )
        LiveWorkroomChat(
            booking = fallbackBooking,
            messages = emptyList(),
            currentUserRole = uiState.currentRole,
            currentUserId = uiState.currentUser?.id ?: "usr_cust_1",
            language = uiState.currentLanguage,
            onBack = onBackClick,
            onSendMessage = { text, attachmentUrl, attachmentType, durationSec ->
                viewModel.sendWorkroomChatMessage(
                    fallbackBooking.id,
                    text,
                    attachmentUrl,
                    attachmentType,
                    durationSec
                )
            },
            modifier = modifier
        )
    }
}
