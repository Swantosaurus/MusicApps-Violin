package com.kobera.music.common.sound.di

import com.kobera.music.common.sound.FrequencyReader
import com.kobera.music.common.sound.PcmAudioRecorder
import org.koin.dsl.module

val soundModule = module {
    single { PcmAudioRecorder(applicationContext = get()) }
    factory { FrequencyReader(pcmAudioRecorder = get()) }
}