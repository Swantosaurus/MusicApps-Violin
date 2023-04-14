package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature


interface Scale {
    fun getKeySignature() : KeySignature
    fun getNotes(): List<SheetNote>
}

