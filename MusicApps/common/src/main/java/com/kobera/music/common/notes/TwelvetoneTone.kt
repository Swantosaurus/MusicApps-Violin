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

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation version 3 of the License, or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */