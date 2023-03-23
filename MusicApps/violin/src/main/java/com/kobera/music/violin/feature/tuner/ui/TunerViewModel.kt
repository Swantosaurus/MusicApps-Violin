package com.kobera.music.violin.feature.tuner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kobera.music.common.sound.FrequencyReader
import com.kobera.music.common.sound.PcmAudioRecorder
import kotlinx.coroutines.launch

class TunerViewModel(private val frequencyReader: FrequencyReader): ViewModel() {



    fun startRecording() {
        frequencyReader.start()
    }

    fun stopRecording() {
        frequencyReader.stop()
    }

}