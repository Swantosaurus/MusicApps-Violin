package com.kobera.music.common.model

import org.koin.dsl.module

val gamesSensitivityModule = module {
    single { GamesAudioSensitivityStorage(applicationContext = get()) }
}