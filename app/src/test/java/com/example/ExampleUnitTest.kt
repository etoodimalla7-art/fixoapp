package com.example

import com.example.data.local.FixoSeedData
import com.example.data.model.SubscriptionTier
import com.example.data.model.formatFixoCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCurrencyFormattingXAF() {
        val formatted = formatFixoCurrency(15000.0)
        assertTrue(formatted.contains("15") && formatted.contains("FCFA"))
    }

    @Test
    fun testInitialUserBalancesAreZero() {
        // Financial integrity: initial users must not have fake balances
        for (user in FixoSeedData.defaultUsers) {
            assertEquals("User ${user.name} must start with 0 balance", 0.0, user.balance, 0.001)
            assertEquals("User ${user.name} must start with 0 escrowLocked", 0.0, user.escrowLocked, 0.001)
        }
    }

    @Test
    fun testSubscriptionTierPricesInXAF() {
        assertEquals(0.0, SubscriptionTier.STARTER.priceMonthly, 0.001)
        assertEquals(10000.0, SubscriptionTier.PRO.priceMonthly, 0.001)
        assertEquals(25000.0, SubscriptionTier.PREMIUM.priceMonthly, 0.001)
    }

    @Test
    fun testEscrowNetPayoutCalculation() {
        val jobAmount = 20000.0
        val netPayout = jobAmount * 0.90
        val platformFee = jobAmount * 0.10
        assertEquals(18000.0, netPayout, 0.001)
        assertEquals(2000.0, platformFee, 0.001)
    }
}
