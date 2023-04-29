@file:Suppress("MagicNumber")
package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature

/**
 * List of minor scales
 */
enum class MinorScale : Scale {
    Ab {
        override fun getKeySignature() = KeySignature.Flats(7)
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
        override fun getKeySignature() = KeySignature.Flats(6)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.Gb,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.Cb,
            SheetNote.Db
        )
    },
    Bb {
        override fun getKeySignature() = KeySignature.Flats(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.Db,
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.Gb,
            SheetNote.Ab
        )
    },
    F {
        override fun getKeySignature() = KeySignature.Flats(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.F,
            SheetNote.G,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.Db,
            SheetNote.Eb
        )
    },
    C {
        override fun getKeySignature() = KeySignature.Flats(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.C,
            SheetNote.D,
            SheetNote.Eb,
            SheetNote.F,
            SheetNote.G,
            SheetNote.Ab,
            SheetNote.Bb
        )
    },
    G {
        override fun getKeySignature() = KeySignature.Flats(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.G,
            SheetNote.A,
            SheetNote.Bb,
            SheetNote.C,
            SheetNote.D,
            SheetNote.Eb,
            SheetNote.F
        )
    },
    D {
        override fun getKeySignature() = KeySignature.Flats(1)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.D,
            SheetNote.E,
            SheetNote.F,
            SheetNote.G,
            SheetNote.A,
            SheetNote.Bb,
            SheetNote.C
        )
    },
    A {
        override fun getKeySignature() = KeySignature.Sharps(0)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.A,
            SheetNote.B,
            SheetNote.C,
            SheetNote.D,
            SheetNote.E,
            SheetNote.F,
            SheetNote.G
        )
    },
    E {
        override fun getKeySignature() = KeySignature.Sharps(1)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.G,
            SheetNote.A,
            SheetNote.B,
            SheetNote.C,
            SheetNote.D
        )
    },
    B {
        override fun getKeySignature() = KeySignature.Sharps(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.D,
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.G,
            SheetNote.A
        )
    },
    FSharp {
        override fun getKeySignature() = KeySignature.Sharps(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.D,
            SheetNote.E
        )
    },
    CSharp {
        override fun getKeySignature() = KeySignature.Sharps(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.E,
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.A,
            SheetNote.B
        )
    },
    GSharp {
        override fun getKeySignature() = KeySignature.Sharps(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.GSharp,
            SheetNote.ASharp,
            SheetNote.B,
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.E,
            SheetNote.FSharp
        )
    },
    DSharp {
        override fun getKeySignature() = KeySignature.Sharps(6)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.DSharp,
            SheetNote.ESharp,
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.ASharp,
            SheetNote.B,
            SheetNote.CSharp
        )
    },
    ASharp {
        override fun getKeySignature() = KeySignature.Sharps(7)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.ASharp,
            SheetNote.BSharp,
            SheetNote.CSharp,
            SheetNote.DSharp,
            SheetNote.ESharp,
            SheetNote.FSharp,
            SheetNote.GSharp
        )
    }
}
