package com.kobera.music.violin

import android.app.Application
import com.kobera.music.common.metronome.di.metronomeModule
import com.kobera.music.common.model.gamesSensitivityModule
import com.kobera.music.common.notes.TwelveToneNames
import com.kobera.music.common.resource.di.resourcesModule
import com.kobera.music.common.score.di.scoreModule
import com.kobera.music.common.sound.di.soundModule
import com.kobera.music.violin.feature.homepage.di.startingScreenModule
import com.kobera.music.violin.feature.recognizeNote.di.recognizeNoteModule
import com.kobera.music.violin.feature.scales.scalesModule
import com.kobera.music.violin.feature.tuner.di.tunerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * entry point of the application
 */
class ViolinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        TwelveToneNames.setNamesFromLowestToHighest(
            arrayOf(
                getString(R.string.note_c),
                getString(R.string.note_c_sharp),
                getString(R.string.cote_d),
                getString(R.string.note_d_sharp),
                getString(R.string.note_e),
                getString(R.string.note_f),
                getString(R.string.note_f_sharp),
                getString(R.string.note_g),
                getString(R.string.note_g_sharp),
                getString(R.string.note_a),
                getString(R.string.note_a_sharp),
                getString(R.string.note_b)
            )
        )

        startKoin {
            androidContext(this@ViolinApplication)
            modules(
                tunerModule + soundModule + resourcesModule + recognizeNoteModule +
                        gamesSensitivityModule + scoreModule + startingScreenModule
                + metronomeModule + scalesModule
            )
        }
    }
}
