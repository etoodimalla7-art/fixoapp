package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.remote.NetworkClient
import com.example.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fixo_auth_session", Context.MODE_PRIVATE)

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

    private val _isDevEnvironment = MutableStateFlow(prefs.getBoolean(KEY_IS_DEV_ENV, false))
    val isDevEnvironment: StateFlow<Boolean> = _isDevEnvironment.asStateFlow()

    init {
        val token = prefs.getString(KEY_ACCESS_TOKEN, null)
        NetworkClient.setAuthToken(token)
    }

    fun setDevEnvironment(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DEV_ENV, enabled).apply()
        _isDevEnvironment.value = enabled
    }

    fun saveSession(userId: String, role: UserRole, accessToken: String, refreshToken: String) {
        prefs.edit()
            .putBoolean(KEY_IS_AUTH, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_ROLE, role.name)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()

        NetworkClient.setAuthToken(accessToken)
        _currentUserId.value = userId
        _currentRole.value = role
        _isAuthenticated.value = true
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
        prefs.edit()
            .putBoolean(KEY_IS_AUTH, false)
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()

        NetworkClient.setAuthToken(null)
        _isAuthenticated.value = false
    }

    companion object {
        private const val KEY_IS_AUTH = "key_is_auth"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_ACCESS_TOKEN = "key_access_token"
        private const val KEY_REFRESH_TOKEN = "key_refresh_token"
        private const val KEY_APP_LANG = "key_app_lang"
        private const val KEY_IS_DEV_ENV = "key_is_dev_env"
    }
}
