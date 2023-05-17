package com.kobera.music.common.sound.di

import com.kobera.music.common.sound.PcmAudioRecorder
import com.kobera.music.common.sound.f0Readers.SingleFrequencyReaderWorker
import com.kobera.music.common.sound.f0Readers.YinSingleFrequencyReader
import com.kobera.music.common.sound.frequency.A4Frequency
import com.kobera.music.common.sound.yin.FastYin
import org.koin.dsl.module

val soundModule = module {
    single { A4Frequency(applicationContext = get()) }
    single { PcmAudioRecorder(applicationContext = get()) }
    /**
     * there you can swap SingleFrequency readers between testing for generator and for violin
     */
    single { FastYin(sampleRate = PcmAudioRecorder.sampleRate) }

    factory<SingleFrequencyReaderWorker> {
        //ViolinSingleFrequencyReader(pcmAudioRecorder = get())
        //SoundGeneratorFrequencyReader(pcmAudioRecorder = get())
        YinSingleFrequencyReader(pcmAudioRecorder = get(), yin = get())
    }
}

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation version 3 of the License,
 * or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */
