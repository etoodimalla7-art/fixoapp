package com.example.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.JobStatus
import com.example.data.model.UserRole
import com.example.data.model.isWorking
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.JobStatusBadge
import com.example.ui.screens.worker.PhotoInspectionValidator
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate800
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DrawnAnnotation(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float = 8f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkroomChat(
    booking: Booking,
    messages: List<ChatMessage>,
    currentUserRole: UserRole,
    currentUserId: String,
    language: AppLanguage,
    onBack: () -> Unit,
    onSendMessage: (text: String, attachmentUrl: String?, attachmentType: String?, durationSec: Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    var isRecordingPtt by remember { mutableStateOf(false) }
    var pttSeconds by remember { mutableIntStateOf(0) }
    var showMaskedCallDialog by remember { mutableStateOf(false) }
    var showAnnotationDialog by remember { mutableStateOf(false) }
    var selectedPhotoForAnnotation by remember { mutableStateOf(booking.beforePhotoUrl ?: "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600&auto=format&fit=crop") }

    val listState = rememberLazyListState()

    // Chronomètre de Mission (Actif seulement pendant WORK_IN_PROGRESS / IN_PROGRESS si photo initiale validée)
    var elapsedSeconds by remember { mutableIntStateOf(24 * 60 + 15) } // Démarré à 00:24:15 selon spécification
    val isTimerActive = booking.status.isWorking && PhotoInspectionValidator.canStartWork(booking.beforePhotoUrl)

    LaunchedEffect(isTimerActive) {
        if (isTimerActive) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    // Auto-scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // PTT Counter loop
    LaunchedEffect(isRecordingPtt) {
        if (isRecordingPtt) {
            pttSeconds = 0
            while (isRecordingPtt && pttSeconds < 60) {
                delay(1000)
                pttSeconds++
            }
            if (pttSeconds >= 60) {
                isRecordingPtt = false
                onSendMessage("🎤 Note vocale ($pttSeconds s)", "local_audio_stream.m4a", "VOICE", pttSeconds)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Salon de Chantier en Direct",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FixoEmerald500)
                            )
                        }
                        Text(
                            text = if (currentUserRole == UserRole.CUSTOMER) "Artisan : ${booking.workerName}" else "Client : ${booking.customerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Bouton d'appel audio masqué (VoIP)
                    IconButton(
                        onClick = { showMaskedCallDialog = true },
                        modifier = Modifier.testTag("masked_call_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FixoEmerald500.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Appel Audio Masqué",
                                tint = FixoEmerald600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Chat Input Bar with Push-to-Talk and Photo Annotation
            Surface(
                tonalElevation = 6.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // PTT recording state banner
                    AnimatedVisibility(visible = isRecordingPtt) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FixoRed500.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(FixoRed500)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Enregistrement vocal PTT : ${pttSeconds}s / 60s",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = FixoRed500
                                )
                            }
                            Text(
                                text = "Relâchez pour envoyer",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Annotation button
                        IconButton(
                            onClick = {
                                selectedPhotoForAnnotation = booking.beforePhotoUrl ?: "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600&auto=format&fit=crop"
                                showAnnotationDialog = true
                            },
                            modifier = Modifier.testTag("open_photo_annotation_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Annoter Photo",
                                tint = FixoGold600
                            )
                        }

                        // Text Field
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Écrire dans le salon direct...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("workroom_chat_input"),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (textInput.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val msg = textInput.trim()
                                    textInput = ""
                                    onSendMessage(msg, null, null, null)
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500)
                                    .testTag("send_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = FixoNavy900
                                )
                            }
                        } else {
                            // Push-To-Talk Button (WhatsApp Style)
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isRecordingPtt) FixoRed500 else FixoEmerald500)
                                    .clickable {
                                        if (isRecordingPtt) {
                                            isRecordingPtt = false
                                            onSendMessage("🎤 Note vocale (${pttSeconds}s)", "voice_note_ptt.m4a", "VOICE", pttSeconds)
                                        } else {
                                            isRecordingPtt = true
                                        }
                                    }
                                    .testTag("push_to_talk_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Push-to-Talk",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Mission Stopwatch Banner
            MissionStopwatchBanner(
                elapsedSeconds = elapsedSeconds,
                isActive = isTimerActive,
                hasBeforePhoto = PhotoInspectionValidator.canStartWork(booking.beforePhotoUrl)
            )

            // 2. Mandatory Zero-Cash Contract Banner
            ZeroCashOmnipresentBanner(language = language)

            // 3. Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages) { message ->
                    WorkroomMessageBubble(
                        message = message,
                        isFromMe = message.senderId == currentUserId || (currentUserRole == message.senderRole),
                        onOpenAnnotation = {
                            selectedPhotoForAnnotation = message.attachmentUrl ?: selectedPhotoForAnnotation
                            showAnnotationDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Modal VoIP Masked Call Dialog
    if (showMaskedCallDialog) {
        MaskedVoipCallDialog(
            otherPartyName = if (currentUserRole == UserRole.CUSTOMER) booking.workerName else booking.customerName,
            otherPartyAvatar = if (currentUserRole == UserRole.CUSTOMER) booking.workerAvatar else "",
            onDismiss = { showMaskedCallDialog = false }
        )
    }

    // Photo Annotation Drawing Dialog
    if (showAnnotationDialog) {
        PhotoAnnotationDialog(
            imageUrl = selectedPhotoForAnnotation,
            onDismiss = { showAnnotationDialog = false },
            onSaveAnnotation = { drawnPointsCount ->
                showAnnotationDialog = false
                onSendMessage(
                    "📍 Annotation sur photo : Panne pointée avec précision ($drawnPointsCount tracés)",
                    selectedPhotoForAnnotation,
                    "ANNOTATED_IMAGE",
                    null
                )
            }
        )
    }
}

@Composable
fun MissionStopwatchBanner(
    elapsedSeconds: Int,
    isActive: Boolean,
    hasBeforePhoto: Boolean
) {
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().testTag("mission_stopwatch_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Chronomètre",
                    tint = if (isActive) FixoEmerald600 else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Chronomètre Mission :",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            if (isActive) {
                Text(
                    text = "⏱️ $timeFormatted",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = FixoEmerald600
                    )
                )
            } else if (!hasBeforePhoto) {
                Text(
                    text = "🔒 Bloqué (Photo Avant Requise)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = FixoAmber500
                )
            } else {
                Text(
                    text = "En attente démarrage",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ZeroCashOmnipresentBanner(language: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("omnipresent_zero_cash_banner"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Fixo Shield",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = FixoStrings.get("workroom.anti_cash_warning", language),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF991B1B)
            )
        }
    }
}

@Composable
fun WorkroomMessageBubble(
    message: ChatMessage,
    isFromMe: Boolean,
    onOpenAnnotation: () -> Unit
) {
    val bubbleColor = if (isFromMe) FixoGold500 else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isFromMe) FixoNavy900 else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isFromMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Text(
            text = "${message.senderName} • ${SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(message.timestamp))}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isFromMe) 14.dp else 2.dp,
                bottomEnd = if (isFromMe) 2.dp else 14.dp
            ),
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Voice note item
                if (message.attachmentType == "VOICE") {
                    VoiceNotePlayerRow(
                        durationSec = message.voiceDurationSeconds ?: 14,
                        isFromMe = isFromMe
                    )
                } else if (message.attachmentType == "IMAGE" || message.attachmentType == "ANNOTATED_IMAGE") {
                    // Image attachment with annotation button
                    AsyncImage(
                        model = message.attachmentUrl,
                        contentDescription = "Inspection Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenAnnotation() }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                } else {
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceNotePlayerRow(durationSec: Int, isFromMe: Boolean) {
    var isPlaying by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition()
    val animatedHeight by transition.animateFloat(
        initialValue = 4f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(
            onClick = { isPlaying = !isPlaying },
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (isFromMe) FixoNavy900 else FixoEmerald500)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play voice note",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Animated Audio Waveform
        Canvas(modifier = Modifier.size(width = 110.dp, height = 24.dp)) {
            val barCount = 14
            val spacing = size.width / barCount
            for (i in 0 until barCount) {
                val h = if (isPlaying) {
                    ((i * 7 + animatedHeight) % 20).coerceIn(4f, 22f)
                } else {
                    (8f + (i % 5) * 3)
                }
                drawLine(
                    color = if (isFromMe) Color(0xFF1E293B) else Color(0xFF0F766E),
                    start = Offset(i * spacing + spacing / 2, (size.height - h) / 2),
                    end = Offset(i * spacing + spacing / 2, (size.height + h) / 2),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "0:${if (durationSec < 10) "0$durationSec" else "$durationSec"}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isFromMe) FixoNavy900 else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MaskedVoipCallDialog(
    otherPartyName: String,
    otherPartyAvatar: String,
    onDismiss: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val callTime = String.format(Locale.US, "%02d:%02d", callSeconds / 60, callSeconds % 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = FixoNavy900,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("masked_call_modal"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Flux Audio Chiffré • Numéros Masqués",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoGold500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, FixoEmerald500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = FixoEmerald500, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = otherPartyName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Text(
                    text = "Appel VoIP en direct ($callTime)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute button
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) Color.White else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) FixoNavy900 else Color.White
                        )
                    }

                    // Hangup button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(FixoRed500)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Hang Up",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Speaker button
                    IconButton(
                        onClick = { isSpeakerOn = !isSpeakerOn },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isSpeakerOn) FixoEmerald500 else Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun PhotoAnnotationDialog(
    imageUrl: String,
    onDismiss: () -> Unit,
    onSaveAnnotation: (pointsCount: Int) -> Unit
) {
    val drawnAnnotations = remember { mutableStateListOf<DrawnAnnotation>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentColor by remember { mutableStateOf(Color.Red) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pointer la Panne (Dessin Tactile)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().testTag("photo_annotation_canvas_view")) {
                Text(
                    text = "Dessinez avec le doigt un cercle rouge ou une flèche jaune pour désigner la fuite ou l'anomalie :",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Color choices: Red or Yellow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { currentColor = Color.Red },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentColor == Color.Red) Color.Red else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("🔴 Cercle Rouge", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = { currentColor = Color.Yellow },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentColor == Color.Yellow) Color(0xFFEAB308) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("🟡 Flèche Jaune", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                    }

                    TextButton(onClick = { drawnAnnotations.clear() }) {
                        Text("Effacer")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas over image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Background for annotation",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(currentColor) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPoints = listOf(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPoints = currentPoints + change.position
                                    },
                                    onDragEnd = {
                                        if (currentPoints.isNotEmpty()) {
                                            drawnAnnotations.add(DrawnAnnotation(currentPoints, currentColor))
                                            currentPoints = emptyList()
                                        }
                                    }
                                )
                            }
                    ) {
                        // Draw saved annotations
                        drawnAnnotations.forEach { annotation ->
                            if (annotation.points.size > 1) {
                                val path = Path().apply {
                                    moveTo(annotation.points.first().x, annotation.points.first().y)
                                    for (i in 1 until annotation.points.size) {
                                        lineTo(annotation.points[i].x, annotation.points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = annotation.color,
                                    style = Stroke(width = annotation.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                        }

                        // Draw active gesture
                        if (currentPoints.size > 1) {
                            val path = Path().apply {
                                moveTo(currentPoints.first().x, currentPoints.first().y)
                                for (i in 1 until currentPoints.size) {
                                    lineTo(currentPoints[i].x, currentPoints[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = currentColor,
                                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveAnnotation(drawnAnnotations.size) },
                colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = FixoNavy900)
            ) {
                Text("Valider et Envoyer au Salon", fontWeight = FontWeight.Bold)
            }
        }
    )
}
