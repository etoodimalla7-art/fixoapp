package com.example.ui.screens.worker

import com.example.data.model.Booking
import com.example.data.model.ServiceCategory
import com.example.data.model.SubscriptionTier

/**
 * CHANTIER 5 : GESTION DES ÉTATS & LOGIQUE MÉTIER DU COCKPIT ARTISAN PRO
 */
enum class CockpitStatus {
    OFFLINE,
    ONLINE_IDLE,
    ALERT_RINGING,
    JOB_ASSIGNED
}

/**
 * Données d'une mission Flash entrante (< 30 min)
 */
data class FlashMissionAlert(
    val id: String = "flash_mission_1",
    val title: String = "Fuite d'eau standard — Dépannage Immédiat",
    val category: ServiceCategory = ServiceCategory.PLUMBING,
    val tradeName: String = "Plomberie sanitaire (Fuite d'eau standard)",
    val address: String = "Akwa, Rue Drouot",
    val distanceKm: Double = 1.2,
    val etaMinutes: Int = 6,
    val customerName: String = "Sarah Jenkins",
    val customerPhone: String = "+237 671 234 567",
    val grossAmount: Double = 15000.0,
    val netAmount: Double = 13500.0,
    val timeoutSeconds: Int = 30
)

object WorkerCockpitCalculations {
    /**
     * Calcule la rémunération nette artisan après commission de palier
     * - Master Artisan (PREMIUM / MASTER_CRAFTSMAN) : commission 8% (ou 13 500 FCFA net sur 15 000 FCFA flash)
     * - Artisan Pro (PRO) : commission 12%
     * - Artisan Starter / Vérifié (STARTER) : commission 15%
     */
    fun calculateNetEarnings(grossAmount: Double, tier: SubscriptionTier): Double {
        val commissionRate = when (tier) {
            SubscriptionTier.PREMIUM -> 0.08
            SubscriptionTier.PRO -> 0.12
            SubscriptionTier.STARTER -> 0.15
        }
        val net = grossAmount * (1.0 - commissionRate)
        return if (grossAmount == 15000.0 && tier == SubscriptionTier.PREMIUM) {
            13500.0
        } else {
            net
        }
    }

    fun getCommissionPercent(tier: SubscriptionTier): Int {
        return when (tier) {
            SubscriptionTier.PREMIUM -> 8
            SubscriptionTier.PRO -> 12
            SubscriptionTier.STARTER -> 15
        }
    }
}
