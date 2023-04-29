package com.kobera.music.common.notes

import com.kobera.music.common.notes.frequency.InTunePrecision
import com.kobera.music.common.notes.frequency.ToneWithFrequency
import kotlin.math.pow

/**
 * Twelve tone generator
 */
object Tones {
    val twelvetoneStepExponent = 1.0 / InnerTwelveToneInterpretation.numberOfTones
    val numberOfTones = InnerTwelveToneInterpretation.numberOfTones

    val twelvtoneStep = 2.0.pow(twelvetoneStepExponent)
    fun getTones(frequencyA4: Double): Map<String, ToneWithFrequency> = //arrayOf(
        getTones(frequencyA4 = frequencyA4, InTunePrecision.HIGH)

    fun getTones(
        frequencyA4: Double,
        inTunePrecision: InTunePrecision
    ): Map<String, ToneWithFrequency> = //arrayOf(
        generateTonesFromC2toB7(
            frequencyA4 = frequencyA4,
            inTuneRange = inTunePrecision
        )

    private fun generateTonesFromC2toB7(
        frequencyA4: Double,
        inTuneRange: InTunePrecision
    ): Map<String, ToneWithFrequency> {
        val tones = HashMap<String, ToneWithFrequency>()

        @Suppress("MagicNumber")
        val c2Frequency = frequencyA4 / 2.0.pow(33.0 * twelvetoneStepExponent)
        var note = InnerTwelveToneInterpretation.C
        for (octave in minOctave..maxOctave) {
            for (j in TwelveToneNames.getNames().indices) {
                val newToneFrequency =
                    c2Frequency * 2.0.pow(
                        ((octave - minOctave) * numberOfTones + j).toDouble() * twelvetoneStepExponent)
                val noteName = "${TwelveToneNames.getNames()[j]}$octave"
                tones[noteName] = ToneWithFrequency(
                    twelveNoteInterpretation = note,
                    name = TwelveToneNames.getNames()[j],
                    octave = octave,
                    frequency = newToneFrequency,
                    inTuneInterval = inTuneRange
                )
                note = note.nextTone()
            }
        }
        return tones
    }

    private const val minOctave = 2
    private const val maxOctave = 7



    const val defaultA4Frequency = 440.0
}


