package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ArtisanKycStatus
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.model.artisanKycStatus
import com.example.data.model.detectCameroonOperator
import com.example.data.repository.SessionManager
import com.example.data.security.SecureTokenStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Chantier2AuthKycTest {

    private lateinit var context: Context
    private lateinit var secureStorage: SecureTokenStorage
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        secureStorage = SecureTokenStorage(context)
        sessionManager = SessionManager(context)
        sessionManager.logout()
    }

    @Test
    fun testCameroonOperatorDetectionMTN() {
        // MTN prefixes: 67x, 68x, 650-654
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("671234567"))
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("679888999"))
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("680123456"))
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("650112233"))
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("654998877"))
        assertEquals(CameroonMobileOperator.MTN_MOMO, detectCameroonOperator("+237 671 23 45 67"))
    }

    @Test
    fun testCameroonOperatorDetectionOrange() {
        // Orange prefixes: 69x, 655-659
        assertEquals(CameroonMobileOperator.ORANGE_MONEY, detectCameroonOperator("691234567"))
        assertEquals(CameroonMobileOperator.ORANGE_MONEY, detectCameroonOperator("699876543"))
        assertEquals(CameroonMobileOperator.ORANGE_MONEY, detectCameroonOperator("655001122"))
        assertEquals(CameroonMobileOperator.ORANGE_MONEY, detectCameroonOperator("659334455"))
        assertEquals(CameroonMobileOperator.ORANGE_MONEY, detectCameroonOperator("+237 699 87 65 43"))
    }

    @Test
    fun testCameroonOperatorDetectionUnknown() {
        assertEquals(CameroonMobileOperator.UNKNOWN, detectCameroonOperator("222123456"))
        assertEquals(CameroonMobileOperator.UNKNOWN, detectCameroonOperator(""))
    }

    @Test
    fun testArtisanKycStatusMapping() {
        // Strict mapping required by specifications:
        // NON COMMENCÉ -> UNVERIFIED
        // EN REVUE -> PENDING
        // APPROUVÉ -> VERIFIED_PRO / MASTER_CRAFTSMAN
        assertEquals(ArtisanKycStatus.NOT_STARTED, VerificationStatus.UNVERIFIED.artisanKycStatus)
        assertEquals(ArtisanKycStatus.IN_REVIEW, VerificationStatus.PENDING.artisanKycStatus)
        assertEquals(ArtisanKycStatus.APPROVED, VerificationStatus.VERIFIED_PRO.artisanKycStatus)
        assertEquals(ArtisanKycStatus.APPROVED, VerificationStatus.MASTER_CRAFTSMAN.artisanKycStatus)
    }

    @Test
    fun testSecureTokenStorageAndSessionCheck() {
        assertFalse(secureStorage.hasValidTokens())

        secureStorage.saveTokens("jwt_access_test_token_abc", "jwt_refresh_test_token_xyz")
        assertTrue(secureStorage.hasValidTokens())
        assertEquals("jwt_access_test_token_abc", secureStorage.getAccessToken())
        assertEquals("jwt_refresh_test_token_xyz", secureStorage.getRefreshToken())

        sessionManager.saveSession(
            userId = "usr_cust_1",
            role = UserRole.CUSTOMER,
            accessToken = "jwt_access_test_token_abc",
            refreshToken = "jwt_refresh_test_token_xyz"
        )
        assertTrue(sessionManager.isAuthenticated.value)
        assertEquals(UserRole.CUSTOMER, sessionManager.currentRole.value)
        assertEquals("usr_cust_1", sessionManager.currentUserId.value)

        sessionManager.logout()
        assertFalse(sessionManager.isAuthenticated.value)
        assertFalse(secureStorage.hasValidTokens())
    }
}
