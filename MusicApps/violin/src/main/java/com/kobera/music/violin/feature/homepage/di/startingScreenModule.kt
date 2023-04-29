package com.kobera.music.violin.feature.homepage.di

import com.kobera.music.violin.feature.homepage.StartingScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val startingScreenModule = module {
    viewModel {
        StartingScreenViewModel(
            scoreRepository = get(),
        )
    }
}
