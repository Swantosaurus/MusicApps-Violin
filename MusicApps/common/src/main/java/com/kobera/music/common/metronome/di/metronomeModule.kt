package com.kobera.music.common.metronome.di

import com.kobera.music.common.metronome.MetronomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val metronomeModule = module {
    viewModel{
        MetronomeViewModel(get())
    }
}
