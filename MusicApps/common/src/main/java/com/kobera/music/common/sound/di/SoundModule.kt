package com.kobera.music.common.sound.di

import com.kobera.music.common.sound.PcmAudioRecorder
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.ViolinSingleFrequencyReader
import com.kobera.music.common.sound.frequency.A4Frequency
import org.koin.dsl.module

val soundModule = module {
    single { A4Frequency(applicationContext = get()) }
    single { PcmAudioRecorder(applicationContext = get()) }
    factory<SingleFrequencyReader> {
        //SingleFrequencyReader(pcmAudioRecorder = get())
        ViolinSingleFrequencyReader(pcmAudioRecorder = get())
        //SoundGeneratorFrequencyReader(pcmAudioRecorder = get())
    }
}
