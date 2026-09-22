package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserRole
import com.example.data.repository.SessionManager
import com.example.data.security.SecureTokenStorage
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FixoSecurityStorageTest {

    private lateinit var context: Context
    private lateinit var secureStorage: SecureTokenStorage
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        secureStorage = SecureTokenStorage(context)
        sessionManager = SessionManager(context)
    }

    @Test
    fun testSecureTokenStorage_savesAndRetrievesTokensCorrectly() {
        val testAccess = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.testAccessToken123"
        val testRefresh = "ref_test_secure_token_abc456"

        secureStorage.saveTokens(testAccess, testRefresh)

        assertEquals(testAccess, secureStorage.getAccessToken())
        assertEquals(testRefresh, secureStorage.getRefreshToken())
        assertTrue(secureStorage.hasValidTokens())

        secureStorage.clearTokens()

        assertNull(secureStorage.getAccessToken())
        assertNull(secureStorage.getRefreshToken())
        assertFalse(secureStorage.hasValidTokens())
    }

    @Test
    fun testSessionManager_purgesLegacyPlaintextTokensFromPrefs() {
        val rawPrefs = context.getSharedPreferences("fixo_auth_session", Context.MODE_PRIVATE)
        // Inject legacy plaintext token
        rawPrefs.edit()
            .putString("key_access_token", "legacy_leak_plaintext_token")
            .putString("key_refresh_token", "legacy_leak_plaintext_refresh")
            .apply()

        // Re-initialize SessionManager, which must purge legacy keys
        val freshSessionManager = SessionManager(context)

        // Verify standard SharedPreferences no longer contains plaintext tokens
        assertFalse(rawPrefs.contains("key_access_token"))
        assertFalse(rawPrefs.contains("key_refresh_token"))
    }

    @Test
    fun testSessionManager_saveSessionAndLogoutLifecycle() {
        val rawPrefs = context.getSharedPreferences("fixo_auth_session", Context.MODE_PRIVATE)
        val userId = "usr_cameroon_42"
        val accessTok = "access_tok_val_789"
        val refreshTok = "refresh_tok_val_987"

        sessionManager.saveSession(userId, UserRole.CUSTOMER, accessTok, refreshTok)

        // Verify session state
        assertTrue(sessionManager.isAuthenticated.value)
        assertEquals(userId, sessionManager.currentUserId.value)
        assertEquals(UserRole.CUSTOMER, sessionManager.currentRole.value)
        assertEquals(accessTok, sessionManager.getAccessToken())
        assertEquals(refreshTok, sessionManager.getRefreshToken())

        // Verify plaintext tokens were NOT written to standard SharedPreferences
        assertNull(rawPrefs.getString("key_access_token", null))
        assertNull(rawPrefs.getString("key_refresh_token", null))

        // Perform logout
        sessionManager.logout()

        // Verify all credentials and authentication flags are purged
        assertFalse(sessionManager.isAuthenticated.value)
        assertNull(sessionManager.getAccessToken())
        assertNull(sessionManager.getRefreshToken())
        assertFalse(rawPrefs.getBoolean("key_is_auth", true))
    }
}
