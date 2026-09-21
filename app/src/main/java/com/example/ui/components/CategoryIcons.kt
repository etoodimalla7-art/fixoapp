package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.ServiceCategory

fun getCategoryVectorIcon(category: ServiceCategory): ImageVector {
    return when (category) {
        ServiceCategory.CLEANING -> Icons.Default.CleaningServices
        ServiceCategory.PLUMBING -> Icons.Default.Plumbing
        ServiceCategory.ELECTRICAL -> Icons.Default.ElectricBolt
        ServiceCategory.BEAUTY -> Icons.Default.Face
        ServiceCategory.HAIR_BRAIDING -> Icons.Default.ContentCut
        ServiceCategory.AC_COOLING -> Icons.Default.AcUnit
        ServiceCategory.APPLIANCE_REPAIR -> Icons.Default.HomeRepairService
        ServiceCategory.PAINTING -> Icons.Default.FormatPaint
        ServiceCategory.MOVING -> Icons.Default.LocalShipping
        ServiceCategory.CAR_SERVICES -> Icons.Default.DirectionsCar
        ServiceCategory.TUTORING -> Icons.Default.School
        ServiceCategory.PHOTOGRAPHY -> Icons.Default.CameraAlt
        ServiceCategory.EVENTS -> Icons.Default.Celebration
        ServiceCategory.TECH_SUPPORT -> Icons.Default.Computer
        ServiceCategory.CONSTRUCTION -> Icons.Default.Construction
        ServiceCategory.OTHER -> Icons.Default.Handyman
    }
}
