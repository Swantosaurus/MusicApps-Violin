package com.kobera.music.violin.feature.sheetMusic

import com.kobera.music.violin.feature.sheetMusic.scales.ScalesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sheetMusicModule = module {
    viewModel {
        ScalesViewModel(applicationContext = get())
    }
}
