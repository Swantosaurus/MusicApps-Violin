package com.kobera.music.violin.feature.recognize_note.di

import com.kobera.music.violin.feature.recognize_note.RecognizeNoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val recognizeNoteModule = module {
    viewModel {
        RecognizeNoteViewModel(
            singleFrequencyReader = get(),
            applicationContext = get(),
            a4Frequency = get(),
            resourceProvider = get(),
            gamesAudioSensitivityStorage = get(),
        )
    }
}