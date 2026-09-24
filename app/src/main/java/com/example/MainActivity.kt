package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.ThemeMode
import com.example.ui.FixoApp
import com.example.ui.FixoViewModel
import com.example.ui.theme.AmbientLightManager
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.rememberAmbientLightSensorLux

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val fixoViewModel: FixoViewModel = viewModel()
      val uiState by fixoViewModel.uiState.collectAsState()
      val systemIsDark = isSystemInDarkTheme()
      val ambientLux by rememberAmbientLightSensorLux()

      val isDarkTheme = AmbientLightManager.resolveIsDark(
        mode = uiState.themeMode,
        systemIsDark = systemIsDark,
        lux = ambientLux
      )

      MyApplicationTheme(darkTheme = isDarkTheme) {
        FixoApp(viewModel = fixoViewModel)
      }
    }
  }
}

