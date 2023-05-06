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
