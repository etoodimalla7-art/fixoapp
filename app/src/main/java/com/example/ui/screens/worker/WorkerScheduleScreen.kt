package com.example.ui.screens.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen

data class DayScheduleItem(
    val dayName: String,
    val dayNumber: String,
    val fullDateString: String,
    val isToday: Boolean = false
)

/**
 * Écran d'Agenda Hebdomadaire & Planning Opérationnel Artisan FIXO
 */
@Composable
fun WorkerScheduleScreen(
    bookings: List<Booking>,
    language: AppLanguage = AppLanguage.FR,
    onSelectBooking: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysOfWeek = remember {
        listOf(
            DayScheduleItem("Lun", "29", "2026-09-29", isToday = true),
            DayScheduleItem("Mar", "30", "2026-09-30"),
            DayScheduleItem("Mer", "01", "2026-10-01"),
            DayScheduleItem("Jeu", "02", "2026-10-02"),
            DayScheduleItem("Ven", "03", "2026-10-03"),
            DayScheduleItem("Sam", "04", "2026-10-04"),
            DayScheduleItem("Dim", "05", "2026-10-05")
        )
    }

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var isAvailableToday by remember { mutableStateOf(true) }
    var morningSlot by remember { mutableStateOf("08:00 - 12:30") }
    var afternoonSlot by remember { mutableStateOf("14:00 - 18:30") }

    val isDark = MaterialTheme.colorScheme.surface.let {
        (0.299 * it.red + 0.587 * it.green + 0.114 * it.blue) < 0.5
    }

    val bgColor = if (isDark) Color(0xFF080C15) else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("worker_schedule_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // En-tête de l'Agenda
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (language == AppLanguage.FR) "Planning & Disponibilités" else "Schedule & Availability",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Agenda Hebdomadaire • Semaine 40" else "Weekly Agenda • Week 40",
                            fontSize = 13.sp,
                            color = textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFEEF2F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = FixoGold500,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Sélecteur horizontal des jours de la semaine
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(daysOfWeek.size) { index ->
                    val day = daysOfWeek[index]
                    val isSelected = selectedDayIndex == index

                    Box(
                        modifier = Modifier
                            .width(62.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected -> FixoGold500
                                    isDark -> Color(0xFF111827)
                                    else -> Color(0xFFFFFFFF)
                                }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) FixoGold500 else cardBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedDayIndex = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.dayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF080C15) else textSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.dayNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color(0xFF080C15) else textPrimary
                            )
                            if (day.isToday) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF080C15) else FixoSuccessGreen)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Commutateur de statut journalier
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isAvailableToday) FixoSuccessGreen else Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAvailableToday) {
                                    if (language == AppLanguage.FR) "🟢 Disponible aujourd'hui" else "🟢 Available Today"
                                } else {
                                    if (language == AppLanguage.FR) "⚪ En repos" else "⚪ Off Duty"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = if (isAvailableToday) {
                                    if (language == AppLanguage.FR) "Prêt à recevoir les urgences et chantiers" else "Ready to receive dispatches & jobs"
                                } else {
                                    if (language == AppLanguage.FR) "Aucune notification d'urgence reçue" else "No emergency alerts received"
                                },
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isAvailableToday,
                        onCheckedChange = { isAvailableToday = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFFFFF),
                            checkedTrackColor = FixoSuccessGreen,
                            uncheckedThumbColor = Color(0xFFCBD5E1),
                            uncheckedTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.testTag("worker_availability_switch")
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Plages horaires configurables
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Plages Horaires Configurables" else "Configurable Time Slots",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Morning slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.FR) "Matinée" else "Morning",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = morningSlot,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = textPrimary
                                )
                            }
                        }

                        // Afternoon slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                                .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.FR) "Après-midi" else "Afternoon",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = afternoonSlot,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section Chantiers réservés pour la journée sélectionnée
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.FR) "Chantiers Réservés (${daysOfWeek[selectedDayIndex].dayName} ${daysOfWeek[selectedDayIndex].dayNumber})" else "Scheduled Jobs",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "${bookings.size} chantiers",
                    fontSize = 12.sp,
                    color = FixoGold500,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Aucun chantier réservé pour ce jour" else "No bookings for this day",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Les nouvelles demandes apparaîtront en temps réel." else "Incoming requests will appear here.",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }
            }
        } else {
            items(bookings) { booking ->
                ScheduleBookingCard(
                    booking = booking,
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    language = language,
                    onClick = { onSelectBooking(booking) }
                )
            }
        }
    }
}

@Composable
private fun ScheduleBookingCard(
    booking: Booking,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("schedule_booking_item_${booking.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FixoGold500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = booking.serviceTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Ref: ${booking.id.takeLast(6).uppercase()}",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }

                Text(
                    text = formatFixoCurrency(booking.priceAmount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = FixoEmerald600
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = booking.address,
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (booking.status) {
                                JobStatus.COMPLETED, JobStatus.CLOSED_CONFIRMED -> FixoSuccessGreen.copy(alpha = 0.15f)
                                JobStatus.IN_PROGRESS, JobStatus.WORK_IN_PROGRESS -> FixoGold500.copy(alpha = 0.15f)
                                else -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (booking.status) {
                            JobStatus.COMPLETED, JobStatus.CLOSED_CONFIRMED -> FixoSuccessGreen
                            JobStatus.IN_PROGRESS, JobStatus.WORK_IN_PROGRESS -> FixoGold500
                            else -> Color(0xFF3B82F6)
                        }
                    )
                }
            }
        }
    }
}
