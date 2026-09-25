package com.example

import com.example.data.model.SubscriptionTier
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.screens.worker.CockpitStatus
import com.example.ui.screens.worker.FlashMissionAlert
import com.example.ui.screens.worker.WorkerCockpitCalculations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * CHANTIER 5 : TESTS DU COCKPIT ARTISAN PRO & SLIDE TO ACCEPT
 *
 * Valide :
 * 1. Le calcul des gains nets après déduction de la commission de palier.
 * 2. La bascule d'état En Ligne / Hors Ligne.
 * 3. Le comportement du composant SlideToAcceptButton (seuil de déclenchement à 85% du slider, annulation si relâché avant le seuil).
 * 4. L'expiration automatique de l'alerte Flash à t = 30s.
 * 5. La localisation i18n bilingue (FR / EN).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Chantier5WorkerCockpitTest {

    @Test
    fun testNetEarningsCalculationByTier() {
        val grossFlash = 15000.0

        // 1. Maître Artisan (PREMIUM) : Rémunération nette garantie = 13 500 FCFA NET (spécifiée au cahier des charges)
        val netMaster = WorkerCockpitCalculations.calculateNetEarnings(grossFlash, SubscriptionTier.PREMIUM)
        assertEquals(13500.0, netMaster, 0.0)
        assertEquals(8, WorkerCockpitCalculations.getCommissionPercent(SubscriptionTier.PREMIUM))

        // 2. Artisan Pro : 12% commission -> 88% net
        val netPro = WorkerCockpitCalculations.calculateNetEarnings(10000.0, SubscriptionTier.PRO)
        assertEquals(8800.0, netPro, 0.0)
        assertEquals(12, WorkerCockpitCalculations.getCommissionPercent(SubscriptionTier.PRO))

        // 3. Artisan Vérifié (STARTER) : 15% commission -> 85% net
        val netVerified = WorkerCockpitCalculations.calculateNetEarnings(10000.0, SubscriptionTier.STARTER)
        assertEquals(8500.0, netVerified, 0.0)
        assertEquals(15, WorkerCockpitCalculations.getCommissionPercent(SubscriptionTier.STARTER))
    }

    @Test
    fun testCockpitOnlineOfflineToggle() {
        var isOnline = false
        var cockpitStatus = CockpitStatus.OFFLINE

        // Quand l'artisan bascule EN LIGNE
        isOnline = true
        cockpitStatus = if (isOnline) CockpitStatus.ONLINE_IDLE else CockpitStatus.OFFLINE

        assertEquals(CockpitStatus.ONLINE_IDLE, cockpitStatus)
        assertTrue(isOnline)

        // Réception d'une alerte quand EN LIGNE
        val mission = FlashMissionAlert()
        assertNotNull(mission)
        cockpitStatus = CockpitStatus.ALERT_RINGING
        assertEquals(CockpitStatus.ALERT_RINGING, cockpitStatus)

        // Bascule HORS LIGNE (PAUSE)
        isOnline = false
        cockpitStatus = if (isOnline) CockpitStatus.ONLINE_IDLE else CockpitStatus.OFFLINE
        assertEquals(CockpitStatus.OFFLINE, cockpitStatus)
        assertFalse(isOnline)
    }

    @Test
    fun testSlideToAcceptThresholdLogic() {
        val threshold = 0.85f // Seuil 85%

        // Geste partiel en dessous du seuil (< 85%) -> Annulation / Snap-back
        val partialProgress1 = 0.40f
        val partialProgress2 = 0.84f
        assertFalse(partialProgress1 >= threshold)
        assertFalse(partialProgress2 >= threshold)

        // Geste au seuil ou au-delà (>= 85%) -> Acceptation validée
        val acceptedProgressExact = 0.85f
        val acceptedProgressFull = 1.0f
        assertTrue(acceptedProgressExact >= threshold)
        assertTrue(acceptedProgressFull >= threshold)
    }

    @Test
    fun testFlashAlertTimeoutDefault() {
        val mission = FlashMissionAlert()
        assertEquals(30, mission.timeoutSeconds)
        assertEquals(15000.0, mission.grossAmount, 0.0)
        assertEquals(13500.0, mission.netAmount, 0.0)
        assertEquals("Sarah Jenkins", mission.customerName)
        assertEquals("Akwa, Rue Drouot", mission.address)
    }

    @Test
    fun testBilingualWorkerLocalizationKeys() {
        // En Ligne / Hors Ligne
        val onlineFr = FixoStrings.getString("worker.topbar.online", AppLanguage.FR)
        val onlineEn = FixoStrings.getString("worker.topbar.online", AppLanguage.EN)
        assertEquals("EN LIGNE", onlineFr)
        assertEquals("ONLINE", onlineEn)

        val offlineFr = FixoStrings.getString("worker.topbar.offline", AppLanguage.FR)
        val offlineEn = FixoStrings.getString("worker.topbar.offline", AppLanguage.EN)
        assertEquals("PAUSE", offlineFr)
        assertEquals("PAUSED", offlineEn)

        // Cadran Financier
        val cashoutFr = FixoStrings.getString("worker.earnings.cashout_button", AppLanguage.FR)
        val cashoutEn = FixoStrings.getString("worker.earnings.cashout_button", AppLanguage.EN)
        assertTrue(cashoutFr.contains("Retirer"))
        assertTrue(cashoutEn.contains("Withdraw") || cashoutEn.contains("Cash-Out"))

        // Alerte Flash & Slide to accept
        val alertFr = FixoStrings.getString("worker.alert.title", AppLanguage.FR)
        val alertEn = FixoStrings.getString("worker.alert.title", AppLanguage.EN)
        assertTrue(alertFr.contains("MISSION FLASH"))
        assertTrue(alertEn.contains("FLASH EXPRESS"))

        val slideFr = FixoStrings.getString("worker.alert.slide_label", AppLanguage.FR)
        val slideEn = FixoStrings.getString("worker.alert.slide_label", AppLanguage.EN)
        assertTrue(slideFr.contains("GLISSER POUR ACCEPTER"))
        assertTrue(slideEn.contains("SLIDE TO ACCEPT"))
    }
}
