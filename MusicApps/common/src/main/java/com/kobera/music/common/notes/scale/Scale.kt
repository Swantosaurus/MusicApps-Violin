package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature

/**
* Represents a sheet scale
*/
interface Scale {
    fun getKeySignature() : KeySignature
    fun getNotes(): List<SheetNote>
}

/**
 * Returns the major scale corresponding given key signature
 */
fun KeySignature.getMajorScale(): Scale =
    when(this) {
        is KeySignature.Sharps ->  this.getMajorScale()
        is KeySignature.Flats ->  this.getMajorScale()
        else -> throw IllegalArgumentException("Not supported key signature: $this")
    }

@Suppress("MagicNumber")
fun KeySignature.Flats.getMajorScale(): Scale {
    return when(numberOfFlats){
        0 -> MajorScale.C
        1 -> MajorScale.F
        2 -> MajorScale.Bb
        3 -> MajorScale.Eb
        4 -> MajorScale.Ab
        5 -> MajorScale.Db
        6 -> MajorScale.Gb
        7 -> MajorScale.Cb
        else -> throw IllegalArgumentException("Not supported number of flats: $numberOfFlats")
    }
}

@Suppress("MagicNumber")
fun KeySignature.Sharps.getMajorScale(): Scale {
    return when(numberOfSharps){
        0 -> MajorScale.C
        1 -> MajorScale.G
        2 -> MajorScale.D
        3 -> MajorScale.A
        4 -> MajorScale.E
        5 -> MajorScale.B
        6 -> MajorScale.FSharp
        7 -> MajorScale.CSharp
        else -> throw IllegalArgumentException("Not supported number of sharps: $numberOfSharps")
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
