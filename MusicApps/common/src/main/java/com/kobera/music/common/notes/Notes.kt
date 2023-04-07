package com.kobera.music.common.notes

import com.kobera.music.common.notes.frequency.NoteWithFrequency
import kotlin.math.pow

object Notes {
    fun getNotes(frequencyA4: Double): Map<String, NoteWithFrequency>  = //arrayOf(
        generateTonesFromC2toB7(frequencyA4 = frequencyA4)

    private fun generateTonesFromC2toB7(frequencyA4: Double): Map<String, NoteWithFrequency> {
        val tones = HashMap<String, NoteWithFrequency>()
        val c2Frequency = frequencyA4 / 2.0.pow(33.0 / 12.0)
        var note = InnerTwelveToneInterpretation.C
        for(octave in 2 .. 7){
            for(j in TwelveToneNoteNames.getNames().indices){
                val newToneFrequency = c2Frequency * 2.0.pow(((octave - 2) * 12 + j).toDouble()/12)
                val noteName = "${TwelveToneNoteNames.getNames()[j]}$octave"
                tones[noteName] = NoteWithFrequency(
                    twelveNoteInterpretation = note,
                    name = TwelveToneNoteNames.getNames()[j],
                    octave = octave,
                    frequency = newToneFrequency
                )
                note = note.nextTone()
            }
        }
        return tones
    }

    val defaultA4Frequency = 440.0
}


