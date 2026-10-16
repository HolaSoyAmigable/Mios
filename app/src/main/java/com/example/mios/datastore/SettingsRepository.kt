package com.example.mios.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "mios_settings")

data class AppSettings(
    val username: String,
    val lockEnabled: Boolean,
    val pinHash: String?,
    val pinSalt: String?
)

class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    private companion object {
        val USERNAME = stringPreferencesKey("username")
        val LOCK_ENABLED = booleanPreferencesKey("lock_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val PIN_SALT = stringPreferencesKey("pin_salt")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            val hash = prefs[PIN_HASH]
            val salt = prefs[PIN_SALT]
            // El candado solo cuenta como activo si realmente existe un hash guardado
            val enabled = (prefs[LOCK_ENABLED] ?: false) && hash != null && salt != null
            AppSettings(
                username = prefs[USERNAME] ?: "",
                lockEnabled = enabled,
                pinHash = hash,
                pinSalt = salt
            )
        }

    suspend fun setUsername(name: String) {
        dataStore.edit { it[USERNAME] = name }
    }

    suspend fun enableLock(hash: String, salt: String) {
        dataStore.edit {
            it[LOCK_ENABLED] = true
            it[PIN_HASH] = hash
            it[PIN_SALT] = salt
        }
    }

    suspend fun disableLock() {
        dataStore.edit {
            it[LOCK_ENABLED] = false
            it.remove(PIN_HASH)
            it.remove(PIN_SALT)
        }
    }
}