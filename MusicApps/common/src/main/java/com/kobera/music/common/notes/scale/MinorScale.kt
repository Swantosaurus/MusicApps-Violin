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
            SheetNote.Cb.octaveUp(),
            SheetNote.Db.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.Fb.octaveUp(),
            SheetNote.Gb.octaveUp()
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
            SheetNote.Cb.octaveUp(),
            SheetNote.Db.octaveUp()
        )
    },
    Bb {
        override fun getKeySignature() = KeySignature.Flats(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Bb,
            SheetNote.C.octaveUp(),
            SheetNote.Db.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.F.octaveUp(),
            SheetNote.Gb.octaveUp(),
            SheetNote.Ab.octaveUp()
        )
    },
    F {
        override fun getKeySignature() = KeySignature.Flats(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.F,
            SheetNote.G,
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C.octaveUp(),
            SheetNote.Db.octaveUp(),
            SheetNote.Eb.octaveUp()
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
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.F.octaveUp()
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
            SheetNote.C.octaveUp()
        )
    },
    A {
        override fun getKeySignature() = KeySignature.Sharps(0)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.A,
            SheetNote.B,
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.F.octaveUp(),
            SheetNote.G.octaveUp()
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
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp()
        )
    },
    B {
        override fun getKeySignature() = KeySignature.Sharps(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.B,
            SheetNote.CSharp.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.FSharp.octaveUp(),
            SheetNote.G.octaveUp(),
            SheetNote.A.octaveUp()
        )
    },
    FSharp {
        override fun getKeySignature() = KeySignature.Sharps(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp()
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
            SheetNote.CSharp.octaveUp(),
            SheetNote.DSharp.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.FSharp.octaveUp()
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
            SheetNote.CSharp.octaveUp()
        )
    },
    ASharp {
        override fun getKeySignature() = KeySignature.Sharps(7)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.ASharp,
            SheetNote.BSharp,
            SheetNote.CSharp.octaveUp(),
            SheetNote.DSharp.octaveUp(),
            SheetNote.ESharp.octaveUp(),
            SheetNote.FSharp.octaveUp(),
            SheetNote.GSharp.octaveUp()
        )
    };

    companion object {

        @Deprecated("Use getAll() instead", replaceWith = ReplaceWith("values()"))
        fun getAll(): List<MinorScale> = values().toList()
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
