package com.kobera.music.common.notes


/**
 * Twelvetone-represantation of notes
 *
 * !! names has to be set before using !! -- names differ in languages
 */
object TwelveToneNames {
    private var names: Array<String>? = null
    fun setNamesFromLowestToHighest(names: Array<String>) {
        assert(names.size == InnerTwelveToneInterpretation.numberOfTones) {
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
