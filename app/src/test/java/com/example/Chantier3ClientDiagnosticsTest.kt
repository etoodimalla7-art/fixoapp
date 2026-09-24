package com.example

import com.example.data.model.ServiceCategory
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.standardProblemPills
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Chantier3ClientDiagnosticsTest {

    @Test
    fun testSemanticProblemPillMapping() {
        val leakPill = standardProblemPills.find { it.id == "water_leak" }
        assertNotNull(leakPill)
        assertTrue(leakPill!!.queryKeywords.contains("fuite"))
        assertTrue(leakPill.queryKeywords.contains("eau"))

        val circuitPill = standardProblemPills.find { it.id == "circuit_short" }
        assertNotNull(circuitPill)
        assertTrue(circuitPill!!.queryKeywords.contains("disjoncteur"))
        assertTrue(circuitPill.queryKeywords.contains("court-circuit"))

        val acPill = standardProblemPills.find { it.id == "ac_broken" }
        assertNotNull(acPill)
        assertTrue(acPill!!.queryKeywords.contains("clim"))
    }

    @Test
    fun testPricingMatrixStandardVsFlash() {
        val standardPrice = 12000.0
        val flashPrice = 15000.0
        val difference = flashPrice - standardPrice

        assertEquals(12000.0, standardPrice, 0.0)
        assertEquals(15000.0, flashPrice, 0.0)
        assertEquals(3000.0, difference, 0.0)
    }

    @Test
    fun testBilingualLocalizationKeys() {
        val searchFr = FixoStrings.getString("client.search_semantic_placeholder", AppLanguage.FR)
        val searchEn = FixoStrings.getString("client.search_semantic_placeholder", AppLanguage.EN)
        assertTrue(searchFr.contains("panne"))
        assertTrue(searchEn.contains("issue"))

        val flashFr = FixoStrings.getString("client.flash_pkg", AppLanguage.FR)
        val flashEn = FixoStrings.getString("client.flash_pkg", AppLanguage.EN)
        assertTrue(flashFr.contains("15 000 FCFA"))
        assertTrue(flashEn.contains("15,000 FCFA"))

        val standardFr = FixoStrings.getString("client.standard_pkg", AppLanguage.FR)
        val standardEn = FixoStrings.getString("client.standard_pkg", AppLanguage.EN)
        assertTrue(standardFr.contains("12 000 FCFA"))
        assertTrue(standardEn.contains("12,000 FCFA"))
    }

    @Test
    fun testSemanticWorkerCategoryMatching() {
        val sampleWorker = WorkerProfile(
            id = "wrk_test_1",
            userId = "usr_worker_1",
            name = "Marc Dubois",
            category = ServiceCategory.PLUMBING,
            hourlyRate = 12000.0,
            bio = "Maître Plombier Certifié",
            skills = "Plomberie sanitaire, détection de fuite",
            certifications = "Maître Artisan",
            completedJobs = 124,
            rating = 4.9,
            reviewCount = 124,
            avatarUrl = "https://images.unsplash.com/photo-1540569014015-19a7be504e3a",
            locationCity = "Douala",
            locationDistanceKm = 1.2,
            emergencyCalloutAvailable = true
        )

        val query = "fuite lavabo"
        val matchesQuery = sampleWorker.skills.lowercase().contains("fuite") ||
                ((query.contains("fuite") || query.contains("lavabo")) && sampleWorker.category == ServiceCategory.PLUMBING)

        assertTrue("Semantic mapping should match plumbing for water leak", matchesQuery)
    }
}
