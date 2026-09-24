package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import coil.compose.AsyncImage
import com.example.data.model.SubscriptionTier
import com.example.data.model.VerificationStatus
import com.example.data.model.WorkerProfile
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoSurfaceGlass
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoDangerRed
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy50
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoNeutral200
import com.example.ui.theme.FixoNeutral300
import com.example.ui.theme.FixoNeutral400
import com.example.ui.theme.FixoNeutral500
import com.example.ui.theme.FixoNeutral600
import com.example.ui.theme.FixoNeutral700
import com.example.ui.theme.FixoWhite

// =========================================================================
// FIXO Brand Button System (CTA 52px thumb zone, linear gold gradient)
// =========================================================================

@Composable
fun FixoGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = FixoGold500.copy(alpha = 0.3f),
                spotColor = FixoGold500.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (enabled && !isLoading) FixoGoldGradient
                else Brush.linearGradient(listOf(FixoNeutral400, FixoNeutral400))
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .testTag("fixo_gold_button"),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = FixoNavy950,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF080C15),
                    letterSpacing = 0.3.sp
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
fun FixoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("fixo_primary_button"),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = FixoNeutral700,
            disabledContentColor = FixoNeutral400
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}

@Composable
fun FixoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("fixo_secondary_button"),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = FixoNeutral400
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FixoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.testTag("fixo_text_button"),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(color = color),
            fontWeight = FontWeight.SemiBold
        )
    }
}

// =========================================================================
// Input Fields
// =========================================================================

@Composable
fun FixoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fixo_text_field"),
            label = { Text(label, style = MaterialTheme.typography.bodySmall) },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoNeutral400
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = FixoTextSecondary,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
        if (isError && errorMessage != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun FixoSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onSearch: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .testTag("fixo_search_field"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = FixoTextSecondary
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() })
                )
            }
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = FixoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Pilule de Filtre Normalisée (Rayon de courbure 24 dp)
 * Standard ergonomique tout-terrain pour filtres & sélections rapides au pouce.
 */
@Composable
fun FixoFilterPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("fixo_filter_pill_$text"),
        shape = RoundedCornerShape(24.dp),
        color = if (selected) FixoGold500 else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (selected) FixoGold600 else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color(0xFF080C15) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =========================================================================
// Visual Indicators & Badges
// =========================================================================

@Composable
fun FixoRatingView(
    rating: Double,
    reviewsCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = FixoGold500,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (reviewsCount != null) {
            Spacer(Modifier.width(2.dp))
            Text(
                text = "($reviewsCount)",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = FixoNeutral500
            )
        }
    }
}

// =========================================================================
// Content Cards (Clean, Restrained, Non-Overused)
// =========================================================================

@Composable
fun FixoWorkerCard(
    worker: WorkerProfile,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .testTag("fixo_worker_card_${worker.id}"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AsyncImage(
                    model = worker.avatarUrl,
                    contentDescription = worker.name,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                if (worker.emergencyCalloutAvailable) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(FixoEmerald500, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (worker.backgroundVerified) {
                        VerificationBadge(status = VerificationStatus.VERIFIED_PRO)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${worker.category.displayName} • ${worker.locationCity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = FixoNeutral500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FixoRatingView(
                        rating = worker.rating,
                        reviewsCount = worker.reviewCount
                    )
                    Text(
                        text = "${worker.hourlyRate.toInt()} XAF/hr",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// =========================================================================
// States: Loading, Empty, Error, Skeleton
// =========================================================================

@Composable
fun FixoLoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = FixoNeutral500
        )
    }
}

@Composable
fun FixoEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = FixoNeutral400,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = FixoNeutral500,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            FixoPrimaryButton(
                text = actionText,
                onClick = onAction
            )
        }
    }
}

@Composable
fun FixoErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = FixoNeutral500,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        OutlinedButton(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun FixoSkeletonBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
    )
}

// =========================================================================
// MASTER PROMPT ATOMIC COMPONENTS (Dark Obsidian & Gold / Dual-App)
// =========================================================================

/**
 * Surface des Cartes (surface-card: #1A2232) avec Bordure Subtile (1.5px solid rgba(255,255,255,0.16))
 * Rayon de courbure 14px selon charte ergonomique.
 */
@Composable
fun FixoGlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = FixoSurfaceCard,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, FixoBorderSubtle, RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor
    ) {
        content()
    }
}

/**
 * Interrupteur lumineux En Ligne / Hors Ligne (🟢 EN LIGNE / ⚪ HORS LIGNE)
 * Pilule normalisée à 24 dp.
 */
@Composable
fun FixoOnlineOfflineSwitch(
    isOnline: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (isOnline) FixoSuccessGreen.copy(alpha = 0.15f) else Color(0xFF1E293B))
            .border(1.5.dp, if (isOnline) FixoSuccessGreen else Color(0x33FFFFFF), RoundedCornerShape(24.dp))
            .clickable { onToggle(!isOnline) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    if (isOnline) FixoSuccessGreen.copy(alpha = pulseAlpha)
                    else Color(0xFF94A3B8)
                )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isOnline) "🟢 EN LIGNE" else "⚪ HORS LIGNE",
            color = if (isOnline) FixoSuccessGreen else FixoTextSecondary,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Tactile Horizontal Slider: "Slide to Accept"
 * [ >>>> Glisser pour accepter la mission (13 500 FCFA) >>>> ]
 */
@Composable
fun FixoSlideToAccept(
    amountText: String,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isConfirmed by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E293B))
            .border(1.5.dp, FixoGold500.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        // Background track text
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isConfirmed) "MISSION ACCEPTÉE !" else ">>>> Glisser pour accepter ($amountText) >>>>",
                color = if (isConfirmed) FixoSuccessGreen else FixoGold500,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.4.sp
            )
        }

        // Draggable slider thumb (Bouton normalisé à 12 dp)
        Box(
            modifier = Modifier
                .padding(start = with(density) { offsetX.toDp() }.coerceAtLeast(4.dp))
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FixoGoldGradient)
                .pointerInput(isConfirmed) {
                    if (!isConfirmed) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offsetX > 450f) {
                                    isConfirmed = true
                                    onAccept()
                                } else {
                                    offsetX = 0f
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            val newOffset = (offsetX + dragAmount).coerceIn(0f, 600f)
                            offsetX = newOffset
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Glisser pour accepter",
                tint = Color(0xFF080C15),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Intelligent Cameroon Mobile Phone Input (+237 6 XX XX XX XX)
 * Automatically detects and displays [MTN MoMo] (yellow) or [Orange Money] (orange)
 */
@Composable
fun FixoPhoneInputWithDetector(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Clean input to digits
    val cleanDigits = value.filter { it.isDigit() }
    val isMtn = cleanDigits.startsWith("23767") || cleanDigits.startsWith("23768") ||
            cleanDigits.startsWith("237650") || cleanDigits.startsWith("237651") ||
            cleanDigits.startsWith("237652") || cleanDigits.startsWith("237653") || cleanDigits.startsWith("237654") ||
            cleanDigits.startsWith("67") || cleanDigits.startsWith("68") ||
            cleanDigits.startsWith("650") || cleanDigits.startsWith("651") || cleanDigits.startsWith("652") || cleanDigits.startsWith("653") || cleanDigits.startsWith("654")

    val isOrange = cleanDigits.startsWith("23769") || cleanDigits.startsWith("237655") ||
            cleanDigits.startsWith("237656") || cleanDigits.startsWith("237657") || cleanDigits.startsWith("237658") || cleanDigits.startsWith("237659") ||
            cleanDigits.startsWith("69") || cleanDigits.startsWith("655") ||
            cleanDigits.startsWith("656") || cleanDigits.startsWith("657") || cleanDigits.startsWith("658") || cleanDigits.startsWith("659")

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cameroon_phone_input"),
            label = { Text("Numéro Mobile Camerounais", color = FixoTextSecondary) },
            placeholder = { Text("+237 6 XX XX XX XX", color = FixoTextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null, tint = FixoGold500)
            },
            trailingIcon = {
                Row(modifier = Modifier.padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isMtn) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFCC00)
                        ) {
                            Text(
                                text = "MTN MoMo",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    } else if (isOrange) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFF6600)
                        ) {
                            Text(
                                text = "Orange Money",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = FixoTextPrimary,
                unfocusedTextColor = FixoTextPrimary,
                focusedBorderColor = FixoGold500,
                unfocusedBorderColor = FixoBorderSubtle,
                focusedContainerColor = FixoSurfaceCard,
                unfocusedContainerColor = FixoSurfaceCard
            ),
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
            singleLine = true
        )
    }
}

/**
 * 120px Dark Proximity Radar with golden animated pulses
 * Floating badge: "14 techniciens certifiés disponibles à Akwa"
 */
@Composable
fun FixoProximityRadar(
    quarterName: String = "Akwa",
    certifiedCount: Int = 14,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val pulse1 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulse2 by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, FixoBorderSubtle, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing radar rings
        Box(
            modifier = Modifier
                .size((110 * pulse1).dp)
                .clip(CircleShape)
                .background(FixoGold500.copy(alpha = (1f - pulse1) * 0.25f))
        )
        Box(
            modifier = Modifier
                .size((110 * pulse2).dp)
                .clip(CircleShape)
                .background(FixoGold500.copy(alpha = (1f - pulse2) * 0.25f))
        )

        // Center dot
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(FixoGoldGradient)
        )

        // Floating dynamic badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF080C15).copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FixoSuccessGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$certifiedCount techniciens certifiés disponibles à $quarterName",
                    color = FixoTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * 4 Trust Seals (Les 4 Sceaux de Confiance FIXO)
 * 1. Identité Biométrique Validée
 * 2. Casier Judiciaire Vierge
 * 3. Compétences Techniques Certifiées
 * 4. Couverture Fixo Shield
 */
@Composable
fun FixoTrustPassportCard(
    modifier: Modifier = Modifier
) {
    FixoGlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LES 4 SCEAUX DE CONFIANCE FIXO",
                    color = FixoGold500,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(Modifier.height(10.dp))
            val seals = listOf(
                "1. Identité Biométrique Validée (CNI / Passeport vérifié)",
                "2. Casier Judiciaire Vierge (Bulletin n°3 vérifié)",
                "3. Compétences Techniques Certifiées (Diplôme / Test validé)",
                "4. Couverture Fixo Shield (Assurance casse/dégâts matériels incluse)"
            )
            seals.forEach { seal ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoSuccessGreen, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = seal,
                        color = FixoTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Dynamic Encrypted QR Invoice Card + Massive 4-Digit PIN Code (e.g. 8 4 2 9)
 */
@Composable
fun FixoDynamicInvoiceQrCard(
    bookingId: String,
    amountText: String,
    pinCode: String = "8429",
    isScanWaiting: Boolean = true,
    modifier: Modifier = Modifier
) {
    FixoGlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "FACTURE SÉCURISÉE FIXO",
                color = FixoGold500,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Montant forfaitaire : $amountText",
                color = FixoTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(16.dp))

            // Simulated High-Contrast QR Code box
            Surface(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "QR Code Facture",
                        tint = Color.Black,
                        modifier = Modifier.size(160.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "CODE PIN DE SECOURS",
                color = FixoTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pinCode.forEach { digit ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500)
                    ) {
                        Text(
                            text = digit.toString(),
                            color = FixoGold500,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = if (isScanWaiting) "En attente du scan du client..." else "✅ Paiement validé !",
                color = if (isScanWaiting) FixoGold500 else FixoSuccessGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

