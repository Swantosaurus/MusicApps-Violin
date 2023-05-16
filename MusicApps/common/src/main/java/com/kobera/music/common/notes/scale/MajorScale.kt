@file:Suppress("MagicNumber")

package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import kotlinx.serialization.Serializable


/**
 * List of MajorScales
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
            SheetNote.Cb.octaveUp(),
            SheetNote.Db.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.F.octaveUp()
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
            SheetNote.C.octaveUp()
        )
    },
    Ab {
        override fun getKeySignature() = KeySignature.Flats(4)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Ab,
            SheetNote.Bb,
            SheetNote.C.octaveUp(),
            SheetNote.Db.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.F.octaveUp(),
            SheetNote.G.octaveUp()
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
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp()
        )
    },
    Bb {
        override fun getKeySignature() = KeySignature.Flats(2)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.Bb,
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.Eb.octaveUp(),
            SheetNote.F.octaveUp(),
            SheetNote.G.octaveUp(),
            SheetNote.A.octaveUp()
        )
    },
    F {
        override fun getKeySignature() = KeySignature.Flats(1)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.F,
            SheetNote.G,
            SheetNote.A,
            SheetNote.Bb,
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp()
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
            SheetNote.C.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.FSharp.octaveUp()
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
            SheetNote.CSharp.octaveUp()
        )
    },
    A {
        override fun getKeySignature() = KeySignature.Sharps(3)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.A,
            SheetNote.B,
            SheetNote.CSharp.octaveUp(),
            SheetNote.D.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.FSharp.octaveUp(),
            SheetNote.GSharp.octaveUp()
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
            SheetNote.CSharp.octaveUp(),
            SheetNote.DSharp.octaveUp()
        )
    },
    B {
        override fun getKeySignature() = KeySignature.Sharps(5)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.B,
            SheetNote.CSharp.octaveUp(),
            SheetNote.DSharp.octaveUp(),
            SheetNote.E.octaveUp(),
            SheetNote.FSharp.octaveUp(),
            SheetNote.GSharp.octaveUp(),
            SheetNote.ASharp.octaveUp()
        )
    },
    FSharp {
        override fun getKeySignature() = KeySignature.Sharps(6)
        override fun getNotes(): List<SheetNote> = listOf(
            SheetNote.FSharp,
            SheetNote.GSharp,
            SheetNote.ASharp,
            SheetNote.B,
            SheetNote.CSharp.octaveUp(),
            SheetNote.DSharp.octaveUp(),
            SheetNote.ESharp.octaveUp()
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
    };

    companion object {
        @Deprecated("Use .values() instead", ReplaceWith("values()"))
        fun getAll(): List<MajorScale> = values().toList()
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
