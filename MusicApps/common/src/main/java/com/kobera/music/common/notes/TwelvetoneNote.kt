package com.kobera.music.common.notes

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

    infix fun sameNoteAs(other: TwelvetoneNote): Boolean {
        return this.twelveNoteInterpretation == other.twelveNoteInterpretation && this.octave == other.octave
    }

    override fun compareTo(other: TwelvetoneNote): Int {
        if (this.octave > other.octave) return 1
        if (this.octave < other.octave) return -1

        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal
    }

    open fun nextNote(): TwelvetoneNote {
        return if (twelveNoteInterpretation == InnerTwelveToneInterpretation.B)
            TwelvetoneNote(InnerTwelveToneInterpretation.C, octave + 1)
        else
            TwelvetoneNote(twelveNoteInterpretation.nextTone(), octave)
    }
}