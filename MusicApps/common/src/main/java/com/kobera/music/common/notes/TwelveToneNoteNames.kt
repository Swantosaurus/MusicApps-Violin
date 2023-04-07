package com.kobera.music.common.notes

import kotlin.math.pow


/**
Twelvetone-represantation of notes

!! names has to be set before using !!

names differ in languages
 */
object TwelveToneNoteNames {
    private var names: Array<String>? = null

    val distance: Double = 2.0.pow(1 / 12)

    fun setNamesFromLowestToHighest(names: Array<String>) {
        assert(names.size == 12) {
            "TwelveToneNotes needs 12 notes!!"
        }
        this.names = names
    }

    fun getNames(): Array<String> {
        assert(names != null) {
            "TwelveToneNotes:names was not set up"
        }
        return names!!
    }

    fun getName(twelveToneInterpretation: InnerTwelveToneInterpretation): String {
        assert(names != null) {
            "TwelveToneNotes:names was not set up"
        }
        return names!![twelveToneInterpretation.ordinal]
    }

    @Deprecated("use InnerTwelveToneInterpretation instead")
    fun getIndex(noteName: String): Int {
        assert(names != null) {
            "TwelveToneNotes:names was not set up"
        }
        return names!!.indexOfFirst { it == noteName }
    }
}
