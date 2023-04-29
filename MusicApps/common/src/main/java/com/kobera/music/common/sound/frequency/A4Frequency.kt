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
