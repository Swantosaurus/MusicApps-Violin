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

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation version 3 of the License,
 * or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */
