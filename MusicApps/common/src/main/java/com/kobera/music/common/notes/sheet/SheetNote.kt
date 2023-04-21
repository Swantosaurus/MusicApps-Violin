package com.kobera.music.common.notes.sheet

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.TwelvetoneNote

/*
 * Represents a note for sheet music.
 */
data class SheetNote(
    val innerSheetNote: InnerSheetNote,
    val noteParams: SheetNoteParams = SheetNoteParams(),
    val octave: Int
) {
    fun toTwelveTone(): TwelvetoneNote {
        val twelveNoteInterpretation = InnerTwelveToneInterpretation.fromSheetNote(
            innerSheetNote = innerSheetNote,
            noteParams = noteParams
        )
        val ordinalWithShift = twelveNoteInterpretation.ordinal + noteParams.accidental.twelveToneShift
        val setOctave = when(true){
            (ordinalWithShift in 0..11) -> octave
            (ordinalWithShift < 0) -> octave - 1
            else -> octave + 1
        }
        return TwelvetoneNote(
            twelveNoteInterpretation = twelveNoteInterpretation,
            octave = setOctave
        )
    }

    operator fun compareTo(other: TwelvetoneNote): Int =
        this.toTwelveTone().compareTo(other)

    operator fun compareTo(other: SheetNote): Int =
        compareTo(other.toTwelveTone())


    fun sheetDifference(other: TwelvetoneNote): Int {
        return innerSheetNote.difference(
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


        val C = SheetNote(
            innerSheetNote = InnerSheetNote.C,
            octave = 4
        )
        val CSharp = SheetNote(
            innerSheetNote = InnerSheetNote.C,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Cb = SheetNote(
            innerSheetNote = InnerSheetNote.C,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
        val D = SheetNote(
            innerSheetNote = InnerSheetNote.D,
            octave = 4
        )
        val DSharp = SheetNote(
            innerSheetNote = InnerSheetNote.D,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Db = SheetNote(
            innerSheetNote = InnerSheetNote.D,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
        val E = SheetNote(
            innerSheetNote = InnerSheetNote.E,
            octave = 4
        )
        val ESharp = SheetNote(
            innerSheetNote = InnerSheetNote.E,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Eb = SheetNote(
            innerSheetNote = InnerSheetNote.E,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
        val F = SheetNote(
            innerSheetNote = InnerSheetNote.F,
            octave = 4
        )
        val FSharp = SheetNote(
            innerSheetNote = InnerSheetNote.F,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Fb = SheetNote(
            innerSheetNote = InnerSheetNote.F,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
        val G = SheetNote(
            innerSheetNote = InnerSheetNote.G,
            octave = 4
        )
        val GSharp = SheetNote(
            innerSheetNote = InnerSheetNote.G,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Gb = SheetNote(
            innerSheetNote = InnerSheetNote.G,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.DoubleFlat),
            octave = 4
        )
        val A = SheetNote(
            innerSheetNote = InnerSheetNote.A,
            octave = 4
        )
        val ASharp = SheetNote(
            innerSheetNote = InnerSheetNote.A,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Ab = SheetNote(
            innerSheetNote = InnerSheetNote.A,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
        val B = SheetNote(
            innerSheetNote = InnerSheetNote.B,
            octave = 4
        )
        val BSharp = SheetNote(
            innerSheetNote = InnerSheetNote.B,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Sharp),
            octave = 4
        )
        val Bb = SheetNote(
            innerSheetNote = InnerSheetNote.B,
            noteParams = SheetNoteParams(accidental = SheetNoteParams.Accidental.Flat),
            octave = 4
        )
    }
}
