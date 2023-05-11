package com.kobera.music.violin.feature.scales

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val scalesModule = module {
    viewModel {
        ScalesViewModel(applicationContext = get())
    }
}
