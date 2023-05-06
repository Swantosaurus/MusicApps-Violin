package com.kobera.music.common.sound.di

import com.kobera.music.common.sound.PcmAudioRecorder
import com.kobera.music.common.sound.SingleFrequencyReader
import com.kobera.music.common.sound.ViolinSingleFrequencyReader
import com.kobera.music.common.sound.frequency.A4Frequency
import org.koin.dsl.module

val soundModule = module {
    single { A4Frequency(applicationContext = get()) }
    single { PcmAudioRecorder(applicationContext = get()) }
    factory<SingleFrequencyReader> {
        //SingleFrequencyReader(pcmAudioRecorder = get())
        ViolinSingleFrequencyReader(pcmAudioRecorder = get())
        //SoundGeneratorFrequencyReader(pcmAudioRecorder = get())
    }
}

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation version 3 of the License, or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */