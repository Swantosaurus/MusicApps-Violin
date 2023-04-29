package com.kobera.music.common.resource.di

import com.kobera.music.common.resource.ResourceProvider
import org.koin.dsl.module

val resourcesModule = module {
    single { ResourceProvider(applicationContext = get()) }
}
