package com.example.mios.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mios.ui.screens.LockScreen
import com.example.mios.ui.screens.LockState
import com.example.mios.ui.screens.SettingsViewModel

@Composable
fun AppRoot(settingsViewModel: SettingsViewModel = viewModel()) {
    val lockState by settingsViewModel.lockState.collectAsState()

    when (lockState) {
        LockState.Loading -> Box(modifier = Modifier.fillMaxSize())
        LockState.Locked -> LockScreen(viewModel = settingsViewModel)
        LockState.Unlocked -> AppNavigation(settingsViewModel = settingsViewModel)
    }
}