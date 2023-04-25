package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature


interface Scale {
    fun getKeySignature() : KeySignature
    fun getNotes(): List<SheetNote>
}

fun KeySignature.getMajorScale(): Scale =
    when(this) {
        is KeySignature.Sharps ->  this.getMajorScale()
        is KeySignature.Flats ->  this.getMajorScale()
        else -> throw IllegalArgumentException("Not supported key signature: $this")
    }

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