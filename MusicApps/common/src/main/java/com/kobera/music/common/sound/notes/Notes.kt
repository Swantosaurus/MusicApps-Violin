package com.kobera.music.common.sound.notes

import kotlin.math.pow

object Notes {
    fun getNotes(frequencyA4: Double): Map<String, Note>  = //arrayOf(
        generateTonesFromC2toB7(frequencyA4 = frequencyA4)

    private fun generateTonesFromC2toB7(frequencyA4: Double): Map<String, Note> {
        val tones = HashMap<String, Note>()
        val c2Frequency = frequencyA4 / 2.0.pow(33.0 / 12.0)
        for(octave in 2 .. 7){
            for(j in Note.twelveToneNotes.indices){
                val newToneFrequency = c2Frequency * 2.0.pow(((octave - 2) * 12 + j).toDouble()/12)
                val noteName = "${Note.twelveToneNotes[j]}$octave"
                tones[noteName] = Note(noteName, newToneFrequency)
            }
        }
        return tones
    }
}


