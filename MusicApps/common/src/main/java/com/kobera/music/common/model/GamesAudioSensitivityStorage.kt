package com.kobera.music.common.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.gamesAudioSensitivityStorage by preferencesDataStore(name = "games_audio_sensitivity_storage")

/**
 * A storage for the games audio sensitivity in preferences.
 *
 * @param applicationContext The application context.
 */
class GamesAudioSensitivityStorage(applicationContext: Context) {
    val dataStore = applicationContext.gamesAudioSensitivityStorage
    val key = floatPreferencesKey("sensitivity")

    @Suppress("MagicNumber")
    val sensitivity = dataStore.data.map { preferences ->
         preferences[key] ?: 0.5f
    }


    /**
     * Updates the sensitivity.
     *
     * @param sensitivity The sensitivity.
     */
    suspend fun updateSensitivity(sensitivity: Float) {
        dataStore.edit { preferences ->
            preferences[key] = sensitivity
        }
    }
}
