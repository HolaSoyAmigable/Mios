package com.example.mios.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mios.data.datastore.AppSettings
import com.example.mios.data.datastore.SettingsRepository
import com.example.mios.ui.theme.ThemeRules
import com.example.mios.ui.theme.ThemeSettings
import com.example.mios.util.AppIconManager
import com.example.mios.util.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val MIN_PIN_LENGTH = 4
const val MAX_PIN_LENGTH = 6

sealed interface LockState {
    data object Loading : LockState
    data object Locked : LockState
    data object Unlocked : LockState
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    // null = todavía leyendo del disco
    val settings: StateFlow<AppSettings?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Tema ya validado: nunca contiene tonos prohibidos
    val themeSettings: StateFlow<ThemeSettings> = settings
        .map { ThemeRules.resolve(it?.borderHue, it?.appHue) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeSettings.Default)

    // Vive solo en memoria: al cerrar la app de verdad, vuelve a pedirse la contraseña
    private val unlocked = MutableStateFlow(false)

    val lockState: StateFlow<LockState> = combine(settings, unlocked) { current, isUnlocked ->
        when {
            current == null -> LockState.Loading
            current.lockEnabled && !isUnlocked -> LockState.Locked
            else -> LockState.Unlocked
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LockState.Loading)

    fun setUsername(name: String) {
        viewModelScope.launch { repository.setUsername(name.trim().take(24)) }
    }

    fun saveTheme(borderHue: Float, appHue: Float, iconId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val safe = ThemeRules.resolve(borderHue, appHue)
            repository.saveTheme(safe.borderHue, safe.appHue)
            onDone()
            // Se cambia el icono al final: puede que el sistema cierre la app
            AppIconManager.apply(getApplication(), iconId)
        }
    }

    fun tryUnlock(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = checkPin(pin)
            if (ok) unlocked.value = true
            onResult(ok)
        }
    }

    fun verifyPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(checkPin(pin)) }
    }

    fun setPin(pin: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) { PasswordHasher.create(pin) }
            repository.enableLock(result.hash, result.salt)
            unlocked.value = true // quien la configura ya está dentro; no hay que bloquearlo
            onDone()
        }
    }

    fun removePin() {
        viewModelScope.launch { repository.disableLock() }
    }

    private suspend fun checkPin(pin: String): Boolean {
        val current = settings.value ?: return false
        val hash = current.pinHash ?: return false
        val salt = current.pinSalt ?: return false
        return withContext(Dispatchers.Default) { PasswordHasher.verify(pin, hash, salt) }
    }
}