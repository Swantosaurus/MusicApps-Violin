package com.kobera.music.violin

import android.app.Application
import com.kobera.music.common.resource.di.resourcesModule
import com.kobera.music.common.sound.di.soundModule
import com.kobera.music.violin.feature.tuner.di.tunerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Tree

class ViolinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        startKoin {
            androidContext(this@ViolinApplication)
            modules(tunerModule + soundModule + resourcesModule)
        }
    }
}