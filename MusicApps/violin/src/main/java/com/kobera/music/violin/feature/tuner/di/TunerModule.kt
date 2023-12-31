package com.kobera.music.violin.feature.tuner.di

import com.kobera.music.violin.feature.tuner.model.TunerSensitivityStorage
import com.kobera.music.violin.feature.tuner.ui.TunerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tunerModule = module {
    single {
        TunerSensitivityStorage(applicationContext = get())
    }
    viewModel {
        TunerViewModel(
            applicationContext = get(),
            frequencyReader = get(),
            a4Frequency = get(),
            tunerSensitivityStorage = get()
        )
    }
}
