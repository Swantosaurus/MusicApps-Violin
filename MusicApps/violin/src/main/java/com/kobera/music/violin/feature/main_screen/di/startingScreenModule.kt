package com.kobera.music.violin.feature.main_screen.di

import com.kobera.music.violin.feature.main_screen.StartingScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val startingScreenModule = module {
    viewModel {
        StartingScreenViewModel(
            scoreRepository = get(),
        )
    }
}