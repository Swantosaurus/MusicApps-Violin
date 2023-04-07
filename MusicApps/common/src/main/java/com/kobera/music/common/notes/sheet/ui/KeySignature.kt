package com.kobera.music.common.notes.sheet.ui

interface KeySignature {
    class Flats(val numberOfFlats: Int) : KeySignature
    class Sharps(val numberOfSharps: Int) : KeySignature
}