package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserRole
import com.example.data.remote.NetworkClient
import com.example.data.security.SecureTokenStorage
import com.example.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    AUTO_LUX
}

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fixo_auth_session", Context.MODE_PRIVATE)

    private val secureStorage = SecureTokenStorage(context)

    private val _isAuthenticated = MutableStateFlow(prefs.getBoolean(KEY_IS_AUTH, false))
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentRole = MutableStateFlow(
        runCatching {
            UserRole.valueOf(prefs.getString(KEY_USER_ROLE, UserRole.CUSTOMER.name) ?: UserRole.CUSTOMER.name)
        }.getOrDefault(UserRole.CUSTOMER)
    )
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentUserId = MutableStateFlow(prefs.getString(KEY_USER_ID, "") ?: "")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentLanguage = MutableStateFlow(
        runCatching {
            AppLanguage.valueOf(prefs.getString(KEY_APP_LANG, AppLanguage.EN.name) ?: AppLanguage.EN.name)
        }.getOrDefault(AppLanguage.EN)
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(
        runCatching {
            ThemeMode.valueOf(prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        }.getOrDefault(ThemeMode.SYSTEM)
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _isDevEnvironment = MutableStateFlow(prefs.getBoolean(KEY_IS_DEV_ENV, false))
    val isDevEnvironment: StateFlow<Boolean> = _isDevEnvironment.asStateFlow()

    // Notification and privacy preferences
    private val _notifJobs = MutableStateFlow(prefs.getBoolean(KEY_NOTIF_JOBS, true))
    val notifJobs: StateFlow<Boolean> = _notifJobs.asStateFlow()

    private val _notifMessages = MutableStateFlow(prefs.getBoolean(KEY_NOTIF_MESSAGES, true))
    val notifMessages: StateFlow<Boolean> = _notifMessages.asStateFlow()

    private val _notifPayments = MutableStateFlow(prefs.getBoolean(KEY_NOTIF_PAYMENTS, true))
    val notifPayments: StateFlow<Boolean> = _notifPayments.asStateFlow()

    private val _shareLocation = MutableStateFlow(prefs.getBoolean(KEY_SHARE_LOCATION, true))
    val shareLocation: StateFlow<Boolean> = _shareLocation.asStateFlow()

    init {
        // Securely retrieve encrypted access token
        val token = secureStorage.getAccessToken()
        NetworkClient.setAuthToken(token)

        // Clean up legacy plaintext tokens if they existed from older versions
        if (prefs.contains(LEGACY_ACCESS_TOKEN) || prefs.contains(LEGACY_REFRESH_TOKEN)) {
            prefs.edit()
                .remove(LEGACY_ACCESS_TOKEN)
                .remove(LEGACY_REFRESH_TOKEN)
                .apply()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setNotificationPref(jobs: Boolean, messages: Boolean, payments: Boolean) {
        prefs.edit()
            .putBoolean(KEY_NOTIF_JOBS, jobs)
            .putBoolean(KEY_NOTIF_MESSAGES, messages)
            .putBoolean(KEY_NOTIF_PAYMENTS, payments)
            .apply()
        _notifJobs.value = jobs
        _notifMessages.value = messages
        _notifPayments.value = payments
    }

    fun setShareLocation(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHARE_LOCATION, enabled).apply()
        _shareLocation.value = enabled
    }

    fun setDevEnvironment(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DEV_ENV, enabled).apply()
        _isDevEnvironment.value = enabled
    }

    fun saveSession(
        userId: String,
        role: UserRole,
        accessToken: String = "",
        refreshToken: String = ""
    ) {
        if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
            // Encrypt real backend tokens into KeyStore vault
            secureStorage.saveTokens(accessToken, refreshToken)
            NetworkClient.setAuthToken(accessToken)
        } else {
            // Unauthenticated or local demo profile: clear any cryptographic tokens
            secureStorage.clearTokens()
            NetworkClient.setAuthToken(null)
        }

        // Store non-sensitive session metadata
        prefs.edit()
            .putBoolean(KEY_IS_AUTH, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_ROLE, role.name)
            .remove(LEGACY_ACCESS_TOKEN)
            .remove(LEGACY_REFRESH_TOKEN)
            .apply()

        _currentUserId.value = userId
        _currentRole.value = role
        _isAuthenticated.value = true
    }

    fun getAccessToken(): String? {
        return secureStorage.getAccessToken()
    }

    fun getRefreshToken(): String? {
        return secureStorage.getRefreshToken()
    }

    fun switchRole(role: UserRole) {
        prefs.edit().putString(KEY_USER_ROLE, role.name).apply()
        _currentRole.value = role
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_APP_LANG, lang.name).apply()
        _currentLanguage.value = lang
    }

    fun logout() {
        // Clear cryptographic token vault
        secureStorage.clearTokens()

        // Invalidate session flags
        prefs.edit()
            .putBoolean(KEY_IS_AUTH, false)
            .remove(LEGACY_ACCESS_TOKEN)
            .remove(LEGACY_REFRESH_TOKEN)
            .apply()

        NetworkClient.setAuthToken(null)
        _isAuthenticated.value = false
    }

    companion object {
        private const val KEY_IS_AUTH = "key_is_auth"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_APP_LANG = "key_app_lang"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_IS_DEV_ENV = "key_is_dev_env"
        private const val KEY_NOTIF_JOBS = "key_notif_jobs"
        private const val KEY_NOTIF_MESSAGES = "key_notif_messages"
        private const val KEY_NOTIF_PAYMENTS = "key_notif_payments"
        private const val KEY_SHARE_LOCATION = "key_share_location"

        // Obsolete legacy keys purged on initialization
        private const val LEGACY_ACCESS_TOKEN = "key_access_token"
        private const val LEGACY_REFRESH_TOKEN = "key_refresh_token"
    }
}
