package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import kotlin.math.abs

/**
 * Transforms a frequency to a NoteWithFrequency that is closest to the given frequency.
 */
object FrequencyToTone {

    @Deprecated("Use findClosestNote(frequency, notes) instead")
    fun transform(
        frequency: Double,
        unknownString: String,
        notes: Collection<ToneWithFrequency>
    ): ToneWithFrequency {
        @Suppress("SwallowedException")
        return try {
            notes.first { it.isInRange(frequency) }
        } catch (e: NoSuchElementException) {
            ToneWithFrequency(
                twelveNoteInterpretation = InnerTwelveToneInterpretation.C,
                name = unknownString,
                octave = 0,
                frequency = frequency
            )
        }
    }

    fun findClosestTone(
        frequency: Double,
        notes: Collection<ToneWithFrequency>
    ): ToneWithFrequency {
        return notes.minBy { abs((frequency / it.frequency ) -1) }
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
