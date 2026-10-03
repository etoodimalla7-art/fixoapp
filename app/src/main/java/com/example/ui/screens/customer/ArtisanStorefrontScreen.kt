package com.example.ui.screens.customer

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.data.model.Reel
import com.example.data.model.ServiceItem
import com.example.data.model.WorkerProfile

/**
 * VITRINE OFFICIELLE DE L'ARTISAN (ARTISAN STOREFRONT)
 * Thème dynamique unifié Light & Dark réactif avec forfait unique garanti 15 000 FCFA.
 */
@Composable
fun ArtisanStorefrontScreen(
    worker: WorkerProfile,
    services: List<ServiceItem>,
    reels: List<Reel>,
    onBack: () -> Unit,
    onBookService: (ServiceItem) -> Unit,
    onWatchReel: (Reel) -> Unit,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null,
    onMessage: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    navController: NavController? = null,
    onViewAllReels: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val artisanId = if (worker.id.isNotBlank()) worker.id else "artisan_marc_dubois"
    WorkerProfileScreen(
        worker = worker,
        services = services,
        reels = reels,
        onBack = onBack,
        onBookService = onBookService,
        onWatchReel = onWatchReel,
        isSaved = isSaved,
        onToggleSave = onToggleSave,
        onMessage = {
            Log.d("FIXO_NAV", "Clic Message -> Navigation Chat Marc Dubois ($artisanId)")
            onNavigateToChat(artisanId)
            if (navController != null) {
                navController.navigate("chat_room/$artisanId")
            }
            onMessage()
        },
        onNavigateToChat = { targetId ->
            Log.d("FIXO_NAV", "ArtisanStorefrontScreen onNavigateToChat -> $targetId")
            onNavigateToChat(targetId)
            if (navController != null) {
                navController.navigate("chat_room/$targetId")
            }
        },
        onViewAllReels = onViewAllReels,
        modifier = modifier
    )
}
