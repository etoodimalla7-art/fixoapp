package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.SubscriptionTier
import com.example.data.model.VerificationStatus
import com.example.ui.theme.FixoAmber100
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue100
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed50
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun VerificationBadge(
    status: VerificationStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg, label) = when (status) {
        VerificationStatus.MASTER_CRAFTSMAN -> Triple(FixoAmber100, FixoAmber600, "Master Artisan")
        VerificationStatus.VERIFIED_PRO -> Triple(FixoEmerald100, FixoEmerald600, "Verified Pro")
        VerificationStatus.PENDING -> Triple(FixoSlate100, FixoSlate500, "In Review")
        VerificationStatus.UNVERIFIED -> Triple(FixoRed50, FixoRed500, "Unverified")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (status == VerificationStatus.MASTER_CRAFTSMAN) Icons.Default.Shield else Icons.Default.Verified,
                contentDescription = label,
                tint = fg,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = fg
                )
            )
        }
    }
}

@Composable
fun JobStatusBadge(
    status: JobStatus,
    modifier: Modifier = Modifier
) {
    val (bg, fg, label) = when (status) {
        JobStatus.REQUESTED -> Triple(FixoBlue100, FixoBlue600, "Requested")
        JobStatus.SCHEDULED -> Triple(FixoBlue100, FixoBlue600, "Scheduled")
        JobStatus.ACCEPTED -> Triple(FixoBlue100, FixoBlue600, "Accepted")
        JobStatus.ON_THE_WAY, JobStatus.EN_ROUTE -> Triple(FixoAmber100, FixoAmber600, "Artisan En Route")
        JobStatus.ARRIVED -> Triple(FixoEmerald100, FixoEmerald600, "Arrived on Site")
        JobStatus.IN_PROGRESS -> Triple(FixoAmber100, FixoAmber600, "Work In Progress")
        JobStatus.COMPLETION_REQUESTED -> Triple(FixoAmber100, FixoAmber600, "Inspection Requested")
        JobStatus.COMPLETED -> Triple(FixoEmerald100, FixoEmerald600, "Completed & Released")
        JobStatus.CANCELLED -> Triple(FixoSlate100, FixoSlate500, "Cancelled")
        JobStatus.DISPUTED -> Triple(FixoRed50, FixoRed500, "Under Review")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = fg
            )
        )
    }
}

@Composable
fun EscrowBadge(
    status: EscrowStatus,
    amount: Double,
    modifier: Modifier = Modifier
) {
    val (bg, fg, label) = when (status) {
        EscrowStatus.HOLDING -> Triple(FixoEmerald100, FixoEmerald600, "Escrow Secured")
        EscrowStatus.RELEASED -> Triple(FixoBlue100, FixoBlue600, "Escrow Released")
        EscrowStatus.REFUNDED -> Triple(FixoSlate100, FixoSlate500, "Refunded")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Escrow",
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label • ${com.example.data.model.formatFixoCurrency(amount)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = fg
                )
            )
        }
    }
}

@Composable
fun StarRatingRow(
    rating: Double,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = FixoAmber500,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.2f", rating),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        if (reviewCount != null) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "($reviewCount)",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FixoSlate500
                )
            )
        }
    }
}
