package com.kobera.music.common.notes.sheet.ui

import com.kobera.music.common.notes.sheet.InnerSheetNote

/**
 * Key signature of sheet music
 */
interface KeySignature {
    /**
     * key signatre of flats in sheet music
     */
    class Flats(val numberOfFlats: Int) : KeySignature {
        init {
            if(numberOfFlats > maxNumberOfAccidentals) {
                error("Unsupported number of flats. Use its equivalent notation with less flats")
            }
        }

        override fun getKeySignatureNotes(): List<InnerSheetNote> =
            flatsOrder.take(numberOfFlats)
    }

    /**
     * key signatre of sharps in sheet music
     */
    class Sharps(val numberOfSharps: Int) : KeySignature {
        init {
            if(numberOfSharps > maxNumberOfAccidentals) {
                error("Unsupported number of sharps. Use its equivalent notation with less sharps")
            }
        }

        override fun getKeySignatureNotes(): List<InnerSheetNote> =
            sharpOrder.take(numberOfSharps)
    }
    companion object {
        private const val maxNumberOfAccidentals = 7
        private val flatsOrder = arrayOf(
            InnerSheetNote.B,
            InnerSheetNote.E,
            InnerSheetNote.A,
            InnerSheetNote.D,
            InnerSheetNote.G,
            InnerSheetNote.C,
            InnerSheetNote.F
        )

        private val sharpOrder = flatsOrder.reversed()
    }
    fun getKeySignatureNotes() : List<InnerSheetNote>
}
