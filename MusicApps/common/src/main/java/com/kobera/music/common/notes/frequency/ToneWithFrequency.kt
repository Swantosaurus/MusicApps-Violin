package com.kobera.music.common.notes.frequency

import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.Tones
import com.kobera.music.common.notes.TwelveToneNames
import com.kobera.music.common.notes.TwelvetoneTone
import timber.log.Timber
import kotlin.math.pow

/**
 * Represents a note with reference tone frequency that should represent
 *
 * @param twelveNoteInterpretation the interpretation of the twelve tone system
 * @param name the name of the note
 * @param octave the octave of the note
 * @param frequency the reference tone frequency of the note
 * @param rangeInterval the interval that is used to determine if a frequency is in close range
 * of the tone that note represents
 * @param inTuneInterval the interval that is used to determine if a frequency is in tune of the note
 */
class ToneWithFrequency(
    twelveNoteInterpretation: InnerTwelveToneInterpretation,
    val name: String,
    octave: Int,
    val frequency: Double,
    private val rangeInterval: InRangePrecision = InRangePrecision.MEDIUM,
    private val inTuneInterval: InTunePrecision = InTunePrecision.HIGH,
) : TwelvetoneTone(
    twelveNoteInterpretation = twelveNoteInterpretation,
    octave = octave
) {
    private val twelveToneNoteNames = TwelveToneNames.getNames()

    /**
     * Returns true if the given frequency is in tune of tone responding to the note.
     *
     * @param compareFreq the frequency to compare
     */
    fun isInTune(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        Timber.d("isInTune: $diff, ${inTuneInterval.oneWayDistance}")
        return diff <= inTuneInterval.oneWayDistance
    }

    /**
     * Returns true if the given frequency is in close range of tone responding to the note.
     *
     * @param compareFreq the frequency to compare
     */
    fun isInRange(compareFreq: Double): Boolean {
        val diff = if (compareFreq > frequency) compareFreq / frequency else frequency / compareFreq
        return diff < rangeInterval.oneWayDistance
    }


    /**
     * Returns the difference angle of the given frequency to the frequency of the note.
     */
    fun getDifferenceAngle(compareFreq: Double, maxAngle: Double): Double {
        val angle = if (rangeInterval.oneWayDistance > maxRangeIntervalForAngle) {
            ((compareFreq / frequency) - 1) / (maxRangeIntervalForAngle - 1) * maxAngle
        } else {
            ((compareFreq / frequency) - 1) / (rangeInterval.oneWayDistance - 1) * maxAngle
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
    override fun nextNote(): ToneWithFrequency {
        val index = twelveToneNoteNames.indexOf(name)
        var newOctave = this.octave
        val nextIndex = if (twelveToneNoteNames.size == index) {
            newOctave++
            0
        } else index + 1
        val nextNoteName = twelveToneNoteNames[nextIndex]
        val nextFrequency = frequency * Tones.twelvtoneStep
        return ToneWithFrequency(
            twelveNoteInterpretation.nextTone(),
            nextNoteName,
            newOctave,
            nextFrequency
        )
    }

    /**
     * Returns clone of this object with possivle changes in values
     */
    @Suppress("LongParameterList")
    fun copy(
        twelveNoteInterpretation: InnerTwelveToneInterpretation = this.twelveNoteInterpretation,
        name : String = this.name,
        octave: Int = this.octave,
        frequency: Double = this.frequency,
        rangeInterval: InRangePrecision = this.rangeInterval,
        inTuneInterval: InTunePrecision = this.inTuneInterval
    ): ToneWithFrequency =
        ToneWithFrequency(
            twelveNoteInterpretation,
            name,
            octave,
            frequency,
            rangeInterval,
            inTuneInterval
        )

    companion object {
        // The max range interval for angle is the interval that is used to determine
        // if a frequency is in close range of the tone that note represents
        private val maxRangeIntervalForAngle = 2.0.pow(1.0 / 12)
    }
}

/**
 * Represents a range of a frequency
 */
sealed interface Range{
    val oneWayDistance: Double
}


/**
 * Represents a range of a frequency to be considered close to the frequency of a note
 */
@Suppress("MagicNumber")
enum class InRangePrecision(override val oneWayDistance: Double): Range {
    LOW(Tones.twelvetoneStepExponent),
    MEDIUM(Tones.twelvtoneStep.pow(1.0 / 2.0)),
    HIGH(Tones.twelvtoneStep.pow(1.0 / 8.0))
}

/**
 * Represents a range of a frequency to be considered in tune to the frequency of a note
 */
@Suppress("MagicNumber")
enum class InTunePrecision(override val oneWayDistance: Double): Range {
    LOW(Tones.twelvtoneStep.pow(8.0/20)),
    MEDIUM(Tones.twelvtoneStep.pow( 3.0/20)),
    HIGH(Tones.twelvtoneStep.pow(1.0 / 20.0))
}




