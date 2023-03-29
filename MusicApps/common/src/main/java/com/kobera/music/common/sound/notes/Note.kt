package com.kobera.music.common.sound.notes

import android.util.Log
import kotlin.math.pow


data class Note(val name: String, val frequency: Double){

    fun isInTune(compareFreq: Double) : Boolean {
        val diff = if(compareFreq > frequency ) compareFreq / frequency else frequency / compareFreq
        return diff <= inTuneInterval
    }
    fun isInRange(comapreFreq: Double) : Boolean {
        val diff = if(comapreFreq > frequency ) comapreFreq / frequency else frequency / comapreFreq
        return diff < rangeInterval
    }

    fun getDifferenceAngle(compareFreq: Double, maxAngle: Double) : Double  =
         ((compareFreq / frequency) - 1) / (rangeInterval-1) * maxAngle


    fun nextNote(){
        val index = Note.twelveToneNotes.indexOf(name)
        val nextIndex = if(index == 11) 0 else index + 1
        val nextNote = Note.twelveToneNotes[nextIndex]
        val nextFrequency = frequency * 2.0.pow(1.0/12.0)
        Note(nextNote, nextFrequency)
    }

    companion object {
        val twelveToneNotes = arrayOf(
            "C",
            "C#",
            "D",
            "D#",
            "E",
            "F",
            "F#",
            "G",
            "G#",
            "A",
            "A#",
            "B"
        )

        val rangeInterval = 2.0.pow(1.0 / 24.0)
        val inTuneInterval = 2.0.pow(1.0 / 240.0)
    }

    private val TAG = this::class.simpleName
}
