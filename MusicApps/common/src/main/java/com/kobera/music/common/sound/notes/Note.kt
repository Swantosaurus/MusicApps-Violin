package com.kobera.music.common.sound.notes

import kotlin.math.pow


data class Note(val name: String, val frequency: Double, val rangeInterval : Double = 2.0.pow(1.0 / 24.0), val inTuneInterval : Double = 2.0.pow(1.0 / 240.0)){

    fun isInTune(compareFreq: Double) : Boolean {
        val diff = if(compareFreq > frequency ) compareFreq / frequency else frequency / compareFreq
        return diff <= inTuneInterval
    }
    fun isInRange(comapreFreq: Double) : Boolean {
        val diff = if(comapreFreq > frequency ) comapreFreq / frequency else frequency / comapreFreq
        return diff < rangeInterval
    }

    fun getDifferenceAngle(compareFreq: Double, maxAngle: Double) : Double  {
        val angle = if(rangeInterval > maxRangeIntervalForAngle) {
            ((compareFreq / frequency) - 1) / (maxRangeIntervalForAngle-1) * maxAngle
        } else {
            ((compareFreq / frequency) - 1) / (rangeInterval - 1) * maxAngle
        }
        return if(angle > maxAngle) {
            maxAngle
        } else if(angle < -maxAngle){
            -maxAngle
        } else angle

    }



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

        private val maxRangeIntervalForAngle = 2.0.pow(1.0/12)
    }

    private val TAG = this::class.simpleName
}
