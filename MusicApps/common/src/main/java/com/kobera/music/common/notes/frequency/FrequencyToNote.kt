package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.Notes

object FrequencyToNote {
    fun transform(
        frequency: Double,
        a4Frequency: Double,
        unknownString: String,
        notes: Collection<NoteWithFrequency> = Notes.getNotes(a4Frequency).values
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
}