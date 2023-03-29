package com.kobera.music.violin.feature.tuner.di

import com.kobera.music.violin.feature.tuner.ui.TunerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tunerModule = module {
    viewModel { TunerViewModel(resourceProvider = get(), frequencyReader = get(), a4Frequency = get()) }
}