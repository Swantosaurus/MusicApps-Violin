package com.kobera.music.common.notes

open class BasicNote(
    val twelveNoteInterpretation: InnerTwelveToneInterpretation,
    val octave: Int
) : Comparable<BasicNote> {

    init {
        assert(octave >= 0) {
            "Octave can not be negative"
        }
    }

    open fun difference(other: BasicNote): Int {
        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal + (this.octave - other.octave) * 12
    }

    infix fun sameNoteAs(other: BasicNote): Boolean {
        return this.twelveNoteInterpretation == other.twelveNoteInterpretation && this.octave == other.octave
    }

    override fun compareTo(other: BasicNote): Int {
        if (this.octave > other.octave) return 1
        if (this.octave < other.octave) return -1

        return this.twelveNoteInterpretation.ordinal - other.twelveNoteInterpretation.ordinal
    }

    open fun nextNote(): BasicNote {
        return if (twelveNoteInterpretation == InnerTwelveToneInterpretation.B)
            BasicNote(InnerTwelveToneInterpretation.C, octave + 1)
        else
            BasicNote(twelveNoteInterpretation.nextTone(), octave)
    }
}