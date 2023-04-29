package com.kobera.music.violin.feature.recognizeNote.di

import com.kobera.music.violin.feature.recognizeNote.RecognizeNoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recognizeNoteModule = module {
    viewModel {
        RecognizeNoteViewModel(
            singleFrequencyReader = get(),
            applicationContext = get(),
            a4Frequency = get(),
            gamesAudioSensitivityStorage = get(),
            scoreRepository = get(),
        )
    }
}
