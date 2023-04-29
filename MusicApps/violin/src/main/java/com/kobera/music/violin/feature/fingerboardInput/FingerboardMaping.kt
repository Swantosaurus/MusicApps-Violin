package com.kobera.music.violin.feature.fingerboardInput

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.TwelvetoneTone
import com.kobera.music.common.notes.scale.Scale
import com.kobera.music.common.notes.sheet.SheetNote.Companion.fromSheetNote
import com.kobera.music.violin.sound.notes.violinStrings

/**
 * Represents a finger position on a string.
 */
data class FingerPosition(
    val positionOffset: Int,
    val fingerNumber: Int,
    val respondingNote: TwelvetoneTone
)

/**
 *  Represents all finger positions on the string
 */
data class SingleStringFingerboard(
    val startPosition: Int,
    val fingerPositions: List<FingerPosition>
)

/**
 * Represents all finger positions on the fingerboard
 */
data class FingerboardPositions(
    val stringsFirgerboardPosition: List<SingleStringFingerboard>
)

/**
 * Maps a scale to a fingerboard positions.
 */
class FingerboardMapping {
    companion object {
        fun toFingerboard(twelvetoneNotes: List<TwelvetoneTone>): FingerboardPositions {
            TODO()
        }

        fun toFingerboard(scale: Scale): FingerboardPositions {
            val twelvetoneNotes = scale.getNotes().map {
                InnerTwelveToneInterpretation.fromSheetNote(
                    it.innerSheetNote,
                    it.noteParams
                )
            }

            return FingerboardPositions(
                violinStrings.map { string ->
                    SingleStringFingerboard(0, getFingerPositionForString(string, twelvetoneNotes))
                }
            )
        }


        private fun getFingerPositionForString(
            string: TwelvetoneTone,
            twelvetoneNotes: List<InnerTwelveToneInterpretation>
        ): List<FingerPosition> {
            val result = mutableListOf<FingerPosition>()
            var currentTwelvetoneNoteIndex: Int? = null
            var fingerNumber = 0

            // contains open string
            twelvetoneNotes.firstOrNull { it == string.twelveNoteInterpretation }?.let {
                result.add(
                    FingerPosition(
                        positionOffset = 0,
                        fingerNumber = 0,
                        string
                    )
                )
                currentTwelvetoneNoteIndex = twelvetoneNotes.indexOf(it)
            }

            for (i in 1..4) {
                if (currentTwelvetoneNoteIndex == null) {
                    currentTwelvetoneNoteIndex = findNextTwelvetone(
                        twelvetoneNotes = twelvetoneNotes,
                        from = string.twelveNoteInterpretation
                    )
                }

                currentTwelvetoneNoteIndex = currentTwelvetoneNoteIndex!! + 1

                if (currentTwelvetoneNoteIndex!! >= twelvetoneNotes.size)
                    currentTwelvetoneNoteIndex = 0

                var twelvetoneNote =
                    TwelvetoneTone(twelvetoneNotes[currentTwelvetoneNoteIndex!!], string.octave)
                var difference = twelvetoneNote.difference(string)

                if (difference < 0) {
                    difference += 12
                    twelvetoneNote = TwelvetoneTone(
                        twelvetoneNotes[currentTwelvetoneNoteIndex!!],
                        string.octave + 1
                    )
                }
                result.add(FingerPosition(difference, i, twelvetoneNote))

            }
            return result.toList()
        }

        private fun findNextTwelvetone(
            twelvetoneNotes: List<InnerTwelveToneInterpretation>,
            from: InnerTwelveToneInterpretation
        ): Int =
            twelvetoneNotes.indexOfFirst { it >= from }.takeIf { it >= 0 } ?: 0

    }
}