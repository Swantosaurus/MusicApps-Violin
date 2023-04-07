package com.kobera.music.common.notes.sheet

import com.kobera.music.common.notes.BasicNote
import com.kobera.music.common.notes.InnerTwelveToneInterpretation


class SheetNote(
    twelveNoteInterpretation: InnerTwelveToneInterpretation,
    octave: Int,
    val innerSheetNote: InnerSheetNote,
    val noteParams: SheetNoteParams = SheetNoteParams()
) : BasicNote(twelveNoteInterpretation = twelveNoteInterpretation, octave = octave) {

    constructor(
        innerSheetNote: InnerSheetNote,
        noteParams: SheetNoteParams,
        octave: Int,
    ) : this(
        twelveNoteInterpretation = InnerTwelveToneInterpretation
            .fromSheetNote(innerSheetNote = innerSheetNote, noteParams = noteParams),
        octave = octave,
        innerSheetNote = innerSheetNote,
        noteParams = noteParams
    )

    constructor(
        twelveNoteInterpretation: InnerTwelveToneInterpretation,
        octave: Int,
    ) : this(
        twelveNoteInterpretation = twelveNoteInterpretation,
        octave = octave,
        innerSheetNote = InnerSheetNote.fromTwelveTone(twelveNoteInterpretation),
        SheetNoteParams(twelveNoteInterpretation = twelveNoteInterpretation)
    )

    fun sheetDifference(other: BasicNote): Int {
        val thisInnerSheetNote = InnerSheetNote.fromTwelveTone(this.twelveNoteInterpretation)

        return thisInnerSheetNote.difference(
            InnerSheetNote.fromTwelveTone(other.twelveNoteInterpretation)
        ) + (this.octave - other.octave) * InnerSheetNote.numberOfNotes
    }

    data class SheetNoteParams(
        val duration: Duration = Duration.Quarter,
        val numberOfDots: Int = 0,
        val accidental: Accidental = Accidental.None
    ) {
        constructor(twelveNoteInterpretation: InnerTwelveToneInterpretation) : this(
            accidental =
            when (true) {
                twelveNoteInterpretation.isSharp -> Accidental.Sharp
                else -> Accidental.None
            }
        )

        init {
            assert(numberOfDots <= 2) {
                "Note can not have more than 2 dots"
            }
        }

        enum class Duration {
            Whole,
            Half,
            Quarter,
            Eighth,
            Sixteenth,
            ThirtySecond
        }

        enum class Accidental(val twelveToneShift: Int) {
            None(0),
            Sharp(1),
            Flat(-1),
            DoubleSharp(2),
            DoubleFlat(-2)
        }

    }

    companion object {
        private fun InnerSheetNote.Companion.fromTwelveTone(twelveTone: InnerTwelveToneInterpretation): InnerSheetNote {
            return when (twelveTone) {
                InnerTwelveToneInterpretation.C -> InnerSheetNote.C
                InnerTwelveToneInterpretation.CSharp -> InnerSheetNote.C
                InnerTwelveToneInterpretation.D -> InnerSheetNote.D
                InnerTwelveToneInterpretation.DSharp -> InnerSheetNote.D
                InnerTwelveToneInterpretation.E -> InnerSheetNote.E
                InnerTwelveToneInterpretation.F -> InnerSheetNote.F
                InnerTwelveToneInterpretation.FSharp -> InnerSheetNote.F
                InnerTwelveToneInterpretation.G -> InnerSheetNote.G
                InnerTwelveToneInterpretation.GSharp -> InnerSheetNote.G
                InnerTwelveToneInterpretation.A -> InnerSheetNote.A
                InnerTwelveToneInterpretation.ASharp -> InnerSheetNote.A
                InnerTwelveToneInterpretation.B -> InnerSheetNote.B
            }
        }

        private fun InnerTwelveToneInterpretation.Companion.fromSheetNote(
            innerSheetNote: InnerSheetNote,
            noteParams: SheetNoteParams
        ): InnerTwelveToneInterpretation {
            val twelveToeBase = when (innerSheetNote) {
                InnerSheetNote.C -> InnerTwelveToneInterpretation.C
                InnerSheetNote.D -> InnerTwelveToneInterpretation.D
                InnerSheetNote.E -> InnerTwelveToneInterpretation.E
                InnerSheetNote.F -> InnerTwelveToneInterpretation.F
                InnerSheetNote.G -> InnerTwelveToneInterpretation.G
                InnerSheetNote.A -> InnerTwelveToneInterpretation.A
                InnerSheetNote.B -> InnerTwelveToneInterpretation.B
            }


            return twelveToeBase.move(noteParams.accidental.twelveToneShift)
        }
    }

}
