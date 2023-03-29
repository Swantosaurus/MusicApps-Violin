package com.kobera.music.common.sound.notes

object FrequencyToNote {
    fun transform(
        frequency: Double,
        a4Frequency: Double = 440.0,
        unknownString: String,
        notes: Collection<Note> = Notes.getNotes(a4Frequency).values
    ): Note {
        return try {
            notes.first { it.isInRange(frequency) }
        } catch (e: NoSuchElementException) {
            Note(
                name = unknownString,
                frequency = frequency
            )
        }
    }
}