package com.example.data.rewards

import com.example.data.model.LoyaltyTier
import com.example.data.model.User
import kotlin.math.min

object RewardsModule {
    const val CASHBACK_RATE = 0.05 // 5%
    const val POINT_VALUE_FCFA = 1.0 // 1 Point = 1 FCFA

    fun calculatePointsEarned(jobAmount: Double): Int {
        return (jobAmount * CASHBACK_RATE).toInt().coerceAtLeast(1)
    }

    fun getTierForPoints(points: Int): LoyaltyTier {
        return when {
            points >= LoyaltyTier.PLATINUM.minPoints -> LoyaltyTier.PLATINUM
            points >= LoyaltyTier.GOLD.minPoints -> LoyaltyTier.GOLD
            points >= LoyaltyTier.SILVER.minPoints -> LoyaltyTier.SILVER
            else -> LoyaltyTier.BRONZE
        }
    }

    fun calculateMaxUsablePoints(userPoints: Int, targetAmount: Double): Int {
        return min(userPoints, targetAmount.toInt())
    }

    fun calculateDiscountFromPoints(pointsToUse: Int): Double {
        return pointsToUse * POINT_VALUE_FCFA
    }

    fun calculateTierDiscount(tier: LoyaltyTier, basePrice: Double, isFlash: Boolean = true): Double {
        return when (tier) {
            LoyaltyTier.GOLD, LoyaltyTier.PLATINUM -> basePrice * 0.10 // 10%
            LoyaltyTier.SILVER -> if (isFlash) basePrice * 0.05 else 0.0 // 5% Flash
            LoyaltyTier.BRONZE -> 0.0
        }
    }
}
