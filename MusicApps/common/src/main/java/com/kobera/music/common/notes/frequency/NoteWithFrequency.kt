package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.TwelveToneNoteNames
import com.kobera.music.common.notes.TwelvetoneNote
import kotlin.math.pow

/**
 * Represents a note with reference tone frequency that should represent
 *
 * @param twelveNoteInterpretation the interpretation of the twelve tone system
 * @param name the name of the note
 * @param octave the octave of the note
 * @param frequency the reference tone frequency of the note
 * @param rangeInterval the interval that is used to determine if a frequency is in close range of the tone that note represents
 * @param inTuneInterval the interval that is used to determine if a frequency is in tune of the note
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

    /**
     * Returns true if the given frequency is in tune of tone responding to the note.
     *
     * @param compareFreq the frequency to compare
     */
    fun isInTune(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        return diff <= inTuneInterval
    }

    /**
     * Returns true if the given frequency is in close range of tone responding to the note.
     *
     * @param compareFreq the frequency to compare
     */
    fun isInRange(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        return diff < rangeInterval
    }


    /**
     * Returns the difference angle of the given frequency to the frequency of the note.
     */
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


    /**
     * Returns the next note in the twelve tone system.
     */
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

    /**
     * Returns clone of this object with possivle changes in values
     */
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
        // The max range interval for angle is the interval that is used to determine if a frequency is in close range of the tone that note represents
        private val maxRangeIntervalForAngle = 2.0.pow(1.0 / 12)
    }
}




