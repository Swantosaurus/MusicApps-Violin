package com.kobera.music.violin.feature.tuner.di

import com.kobera.music.common.sound.PcmAudioRecorder
import com.kobera.music.violin.feature.tuner.ui.TunerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tunerModule = module {
    viewModel { TunerViewModel(frequencyReader = get()) }
}