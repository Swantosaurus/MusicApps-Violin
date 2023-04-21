package com.kobera.music.common.notes.sheet


/**
 * Represents a note in a ovtave in sheet notation.
 */
enum class InnerSheetNote {
    C,
    D,
    E,
    F,
    G,
    A,
    B;

    companion object {
        const val numberOfNotes = 7
    }

    fun difference(other: InnerSheetNote): Int {
        return this.ordinal - other.ordinal
    }
}

