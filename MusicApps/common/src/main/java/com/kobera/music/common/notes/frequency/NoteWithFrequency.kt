package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.TwelveToneNoteNames
import com.kobera.music.common.notes.TwelvetoneNote
import kotlin.math.pow

/**
 * Represents a note with a frequency to compare if its in tune or not.
 */
class NoteWithFrequency(
    twelveNoteInterpretation: InnerTwelveToneInterpretation,
    val name: String,
    octave: Int,
    val frequency: Double,
    private val rangeInterval: Double = 2.0.pow(1.0 / 24.0),
    private val inTuneInterval: Double = 2.0.pow(1.0 / 240.0),
) : TwelvetoneNote(
    twelveNoteInterpretation = twelveNoteInterpretation,
    octave = octave
) {
    private val twelveToneNoteNames = TwelveToneNoteNames.getNames()
    fun isInTune(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        return diff <= inTuneInterval
    }

    fun isInRange(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        return diff < rangeInterval
    }

    fun getDifferenceAngle(compareFreq: Double, maxAngle: Double): Double {
        val angle = if (rangeInterval > maxRangeIntervalForAngle) {
            ((compareFreq / frequency) - 1) / (maxRangeIntervalForAngle - 1) * maxAngle
        } else {
            ((compareFreq / frequency) - 1) / (rangeInterval - 1) * maxAngle
        }
        return if (angle > maxAngle) {
            maxAngle
        } else if (angle < -maxAngle) {
            -maxAngle
        } else angle
    }


    override fun nextNote(): NoteWithFrequency {
        val index = twelveToneNoteNames.indexOf(name)
        var newOctave = this.octave
        val nextIndex = if (twelveToneNoteNames.size == index) {
            newOctave++
            0
        } else index + 1
        val nextNoteName = twelveToneNoteNames[nextIndex]
        val nextFrequency = frequency * 2.0.pow(1.0 / 12.0)
        return NoteWithFrequency(
            twelveNoteInterpretation.nextTone(),
            nextNoteName,
            newOctave,
            nextFrequency
        )
    }

    fun copy(
        twelveNoteInterpretation: InnerTwelveToneInterpretation = this.twelveNoteInterpretation,
        name : String = this.name,
        octave: Int = this.octave,
        frequency: Double = this.frequency,
        rangeInterval: Double = this.rangeInterval,
        inTuneInterval: Double = this.inTuneInterval
    ): NoteWithFrequency =
        NoteWithFrequency(
            twelveNoteInterpretation,
            name,
            octave,
            frequency,
            rangeInterval,
            inTuneInterval
        )

    companion object {
        private val maxRangeIntervalForAngle = 2.0.pow(1.0 / 12)
    }
}




