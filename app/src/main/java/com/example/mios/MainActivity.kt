package com.example.mios

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.mios.navigation.AppRoot
import com.example.mios.ui.screens.SettingsViewModel
import com.example.mios.ui.theme.MiosTheme

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La app siempre es oscura: iconos claros en las barras del sistema
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            val themeSettings by settingsViewModel.themeSettings.collectAsState()
            MiosTheme(themeSettings = themeSettings) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}