package com.example.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.FixoApp
import com.example.ui.FixoViewModel
import com.example.ui.screens.chat.LiveWorkroomChatScreen

object FixoDestinations {
    const val ROOT_APP = "root_app"
    const val CHAT_SCREEN = "chat_screen/{workerId}"
    const val CHAT_ROOM = "chat_room/{workerId}"
    const val CHAT_ROOM_MARC = "chat_room/artisan_marc_dubois"

    fun chatScreen(workerId: String) = "chat_screen/$workerId"
    fun chatRoom(workerId: String) = "chat_room/$workerId"
}

@Composable
fun FixoNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: FixoViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = FixoDestinations.ROOT_APP,
        modifier = modifier
    ) {
        composable(FixoDestinations.ROOT_APP) {
            FixoApp(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = "chat_screen/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: "artisan_marc_dubois"
            Log.d("FIXO_NAV", "NavHost -> Destination chat_screen/$workerId")
            LiveWorkroomChatScreen(
                workerId = workerId,
                onBackClick = {
                    Log.d("FIXO_NAV", "Retour depuis chat_screen/$workerId")
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(
            route = "chat_room/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: "artisan_marc_dubois"
            Log.d("FIXO_NAV", "NavHost -> Destination chat_room/$workerId")
            LiveWorkroomChatScreen(
                workerId = workerId,
                onBackClick = {
                    Log.d("FIXO_NAV", "Retour depuis chat_room/$workerId")
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}

/**
 * Alias FixoNavHost pour compatibilité totale avec les directives et tests.
 */
@Composable
fun FixoNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: FixoViewModel,
    modifier: Modifier = Modifier
) {
    FixoNavGraph(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}
