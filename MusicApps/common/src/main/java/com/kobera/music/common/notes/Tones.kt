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
    fun getTones(frequencyA4: Double): Map<String, ToneWithFrequency> =
        getTones(frequencyA4 = frequencyA4, InTunePrecision.HIGH)

    fun getTones(
        frequencyA4: Double,
        inTunePrecision: InTunePrecision
    ): Map<String, ToneWithFrequency> =
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

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation version 3 of the License,
 * or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */


