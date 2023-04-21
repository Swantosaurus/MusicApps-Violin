package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import kotlinx.serialization.Serializable


/**
 * Represents a major scale.
 */
@Serializable
enum class MajorScale : Scale {
    Cb {
        override fun getKeySignature() = KeySignature.Flats(7)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Cb,
            SheetNote.Db,
            SheetNote.Eb,
            SheetNote.Fb,
            SheetNote.Gb,
            SheetNote.Ab,
            SheetNote.Bb
        )
    },
    Gb {
        override fun getKeySignature() = KeySignature.Flats(6)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Gb,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.Cb,
            SheetNote.Db,
            SheetNote.Eb,
            SheetNote.F
        )
    },
    Db {
        override fun getKeySignature() = KeySignature.Flats(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Db,
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.Gb,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C
        )
    },
    Ab {
        override fun getKeySignature() = KeySignature.Flats(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.Db,
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.G
        )
    },
    Eb {
        override fun getKeySignature() = KeySignature.Flats(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.G,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.D
        )
    },
    Bb {
        override fun getKeySignature() = KeySignature.Flats(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.D,
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.G,
            SheetNote.A
        )
    },
    F {
        override fun getKeySignature() = KeySignature.Flats(1)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.F,
            SheetNote.G,
            SheetNote.A,
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.D,
            SheetNote.E
        )
    },
    C {
        override fun getKeySignature() = KeySignature.Sharps(0)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.C,
            SheetNote.D,
            SheetNote.E,
            SheetNote.F,
            SheetNote.G,
            SheetNote.A,
            SheetNote.B
        )
    },
    G {
        override fun getKeySignature() = KeySignature.Sharps(1)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.G,
            SheetNote.A,
            SheetNote.B,
            SheetNote.C,
            SheetNote.D,
            SheetNote.E,
            SheetNote.FSharp
        )
    },
    D {
        override fun getKeySignature() = KeySignature.Sharps(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.D,
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.G,
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp
        )
    },
    A {
        override fun getKeySignature() = KeySignature.Sharps(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.D,
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.GSharp
        )
    },
    E {
        override fun getKeySignature() = KeySignature.Sharps(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.DSharp
        )
    },
    B {
        override fun getKeySignature() = KeySignature.Sharps(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.ASharp
        )
    },
    FSharp {
        override fun getKeySignature() = KeySignature.Sharps(6)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.ASharp,
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.ESharp
        )
    },
    CSharp {
        override fun getKeySignature() = KeySignature.Sharps(7)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.ESharp,
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.ASharp,
            SheetNote.BSharp
        )
    }
}