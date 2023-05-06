package com.kobera.music.common.sound.frequency

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A class that stores the frequency of A4 to android preferences.
 *
 * @param applicationContext The application context.
 */
class A4Frequency(applicationContext: Context) {
    private var _frequency : MutableStateFlow<Double>
    private val preferences = applicationContext.getSharedPreferences("default_a4frequency", Context.MODE_PRIVATE)
    private val frequencyKey = "frequency"

    init {
        @Suppress("MagicNumber")
        _frequency = MutableStateFlow(
            preferences.getFloat(frequencyKey, 440.0f).toDouble()
        )
    }

    var frequency = _frequency.asStateFlow()

    fun set(to: Double) {
        _frequency.value = to

        writeToPrefs(to)
    }

    private fun writeToPrefs(to: Double){
        with(preferences.edit()){
            putFloat(frequencyKey, to.toFloat())
            apply()
        }
    }
}

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation version 3 of the License, or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */