package com.kobera.music.common.notes.sheet.ui

import com.kobera.music.common.notes.sheet.InnerSheetNote

interface KeySignature {
    class Flats(val numberOfFlats: Int) : KeySignature {
        init {
            if(numberOfFlats >= 8) {
                throw IllegalStateException("unsupported number of flats. Use its eqvivalent notation with less flats")
            }
        }

        override fun getKeySignatureNotes(): List<InnerSheetNote> =
            flatsOrder.take(numberOfFlats)
    }
    class Sharps(val numberOfSharps: Int) : KeySignature {
        init {
            if(numberOfSharps >= 8) {
                throw IllegalStateException("unsupported number of sharps. Use its eqvivalent notation with less sharps")
            }
        }

        override fun getKeySignatureNotes(): List<InnerSheetNote> =
            flatsOrder.take(numberOfSharps)
    }
    companion object {
        private val flatsOrder = arrayOf(
            InnerSheetNote.B,
            InnerSheetNote.E,
            InnerSheetNote.A,
            InnerSheetNote.D,
            InnerSheetNote.G,
            InnerSheetNote.C,
            InnerSheetNote.F
        )

        private val sharpOrder = flatsOrder.reverse()
    }
    abstract fun getKeySignatureNotes() : List<InnerSheetNote>
}