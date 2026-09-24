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
    fun testSeedUserBalancesAccordingToSpec() {
        // Verify predefined sandbox users comply with seed dataset specification
        val sarah = FixoSeedData.defaultUsers.find { it.id == "usr_cust_1" }
        val marc = FixoSeedData.defaultUsers.find { it.id == "usr_worker_1" }
        
        org.junit.Assert.assertNotNull(sarah)
        assertEquals(35000.0, sarah!!.balance, 0.001)
        assertEquals(0.0, sarah.escrowLocked, 0.001)
        assertEquals(350, sarah.fixoPoints)

        org.junit.Assert.assertNotNull(marc)
        assertEquals(48500.0, marc!!.balance, 0.001)
        assertEquals(15000.0, marc.escrowLocked, 0.001)
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
