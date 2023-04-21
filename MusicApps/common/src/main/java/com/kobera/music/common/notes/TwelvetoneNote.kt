package com.kobera.music.common.notes

/**
 * Represents a note in a octave in twelve tone notation.
 */
open class TwelvetoneNote(
    val twelveNoteInterpretation: InnerTwelveToneInterpretation,
    val octave: Int
) : Comparable<TwelvetoneNote> {

    init {
        assert(octave >= 0) {
            "Octave can not be negative"
        }
    }

    open fun difference(other: TwelvetoneNote): Int {
        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal + (this.octave - other.octave) * 12
    }

    /**
     * Returns true if the note is the same as the other note.
     */
    infix fun sameNoteAs(other: TwelvetoneNote): Boolean {
        return this.twelveNoteInterpretation == other.twelveNoteInterpretation && this.octave == other.octave
    }

    /**
     * returns 1 if this note is higher than the other note,
     * -1 if this note is lower than the other note,
     * 0 if they are the same.
     */
    override fun compareTo(other: TwelvetoneNote): Int {
        if (this.octave > other.octave) return 1
        if (this.octave < other.octave) return -1

        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal
    }

    /**
     * Returns the next note in the twelve tone system.
     */
    open fun nextNote(): TwelvetoneNote {
        return if (twelveNoteInterpretation == InnerTwelveToneInterpretation.B)
            TwelvetoneNote(InnerTwelveToneInterpretation.C, octave + 1)
        else
            TwelvetoneNote(twelveNoteInterpretation.nextTone(), octave)
    }
}