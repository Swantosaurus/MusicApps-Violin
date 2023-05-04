package com.kobera.music.common.notes

/**
 * Represents a tone in a octave in twelve tone notation.
 */
open class TwelvetoneTone(
    val twelveNoteInterpretation: InnerTwelveToneInterpretation,
    val octave: Int
) : Comparable<TwelvetoneTone> {

    init {
        assert(octave >= 0) {
            "Octave can not be negative"
        }
    }

    open fun differenceFrom(other: TwelvetoneTone): Int {
        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal +
                (this.octave - other.octave) * Tones.numberOfTones
    }

    open fun differenceTo(other: TwelvetoneTone) = - differenceFrom(other)

    /**
     * Returns true if the note is the same as the other note.
     */
    infix fun sameNoteAs(other: TwelvetoneTone): Boolean {
        return this.twelveNoteInterpretation == other.twelveNoteInterpretation
                && this.octave == other.octave
    }

    /**
     * returns 1 if this note is higher than the other note,
     * -1 if this note is lower than the other note,
     * 0 if they are the same.
     */
    @Suppress("ReturnCount")
    override fun compareTo(other: TwelvetoneTone): Int {
        if (this.octave > other.octave) return 1
        if (this.octave < other.octave) return -1

        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal
    }

    /**
     * Returns the next note in the twelve tone system.
     */
    open fun nextNote(): TwelvetoneTone {
        return if (twelveNoteInterpretation == InnerTwelveToneInterpretation.B)
            TwelvetoneTone(InnerTwelveToneInterpretation.C, octave + 1)
        else
            TwelvetoneTone(twelveNoteInterpretation.nextTone(), octave)
    }
}
