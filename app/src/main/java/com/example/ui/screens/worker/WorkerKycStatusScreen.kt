package com.example.ui.screens.worker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.User
import com.example.localization.AppLanguage

/**
 * Écran officiel de statut d'homologation artisan (WorkerKycStatusScreen)
 * Interface institutionnelle 100 % en français pour la conformité et l'audit.
 */
@Composable
fun WorkerKycStatusScreen(
    user: User?,
    language: AppLanguage = AppLanguage.FR,
    onUpdateDocuments: () -> Unit,
    onContactSupportWhatsapp: () -> Unit,
    onSimulateInstantApproval: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    KycPendingScreen(
        user = user,
        language = language,
        onUpdateDocuments = onUpdateDocuments,
        onContactSupportWhatsapp = onContactSupportWhatsapp,
        onSimulateInstantApproval = onSimulateInstantApproval,
        onLogout = onLogout,
        modifier = modifier
    )
}
