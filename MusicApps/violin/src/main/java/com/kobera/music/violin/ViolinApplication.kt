package com.kobera.music.violin

import android.app.Application
import com.kobera.music.common.sound.di.soundModule
import com.kobera.music.violin.feature.tuner.di.tunerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ViolinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ViolinApplication)
            modules(tunerModule + soundModule)
        }
    }
}