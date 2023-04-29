package com.kobera.music.violin.feature.tuner.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.tunerSensitivityDataStore : DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore("tunerSensitivity")

/**
 * A class that stores the tuner sensitivity to android preferences.
 */
class TunerSensitivityStorage(applicationContext: Context){
    private val dataStore = applicationContext.tunerSensitivityDataStore
    private val key = floatPreferencesKey("sensitivity_value")

    val sensitivity : Flow<Float> = dataStore.data.map { preferences ->
         preferences[key] ?: 0.2f
    }

    suspend fun storeSensitivity(float: Float) {
        dataStore.edit { settings ->
            settings[key] = float
        }
    }
}