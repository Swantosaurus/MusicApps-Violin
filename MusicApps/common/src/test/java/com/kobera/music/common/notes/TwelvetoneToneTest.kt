package com.kobera.music.common.notes

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class TwelvetoneToneTest {

    @Test
    fun compareTo() {
        var previous = TwelvetoneTone(InnerTwelveToneInterpretation.B, 1)
        for (octave in 2..7) {
            for (twelvetone in InnerTwelveToneInterpretation.values()) {
                val tmp = TwelvetoneTone(twelvetone, octave)
                assertTrue(tmp > previous) {
                    "${tmp.twelveNoteInterpretation}, ${tmp.octave} " +
                            "!> ${previous.twelveNoteInterpretation}, ${previous.octave}"
                }
                previous = tmp
            }
        }

        previous = TwelvetoneTone(InnerTwelveToneInterpretation.C, 8)
        for (octave in 7 downTo 2) {
            for (twelvetone in InnerTwelveToneInterpretation.values().reversed()) {
                val tmp = TwelvetoneTone(twelvetone, octave)
                assertTrue(tmp < previous) {
                    "${tmp.twelveNoteInterpretation}, ${tmp.octave} " +
                            "!< ${previous.twelveNoteInterpretation}, ${previous.octave}"
                }
                previous = tmp
            }
        }
    }
}
