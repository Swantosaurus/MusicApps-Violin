package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import kotlin.math.abs

/**
 * Transforms a frequency to a NoteWithFrequency that is closest to the given frequency.
 */
object FrequencyToNote {

    @Deprecated("Use findClosestNote(frequency, notes) instead")
    fun transform(
        frequency: Double,
        unknownString: String,
        notes: Collection<NoteWithFrequency>
    ): NoteWithFrequency {
        return try {
            notes.first { it.isInRange(frequency) }
        } catch (e: NoSuchElementException) {
            NoteWithFrequency(
                twelveNoteInterpretation = InnerTwelveToneInterpretation.C,
                name = unknownString,
                octave = 0,
                frequency = frequency
            )
        }
    }

    fun findClosestNote(
        frequency: Double,
        notes: Collection<NoteWithFrequency>
    ): NoteWithFrequency {
        return notes.minBy { abs((frequency / it.frequency ) -1) }
    }
}