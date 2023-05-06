package com.kobera.music.common.notes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InnerTwelveToneInterpretationTest {

    @Test
    fun nextTone() {
        var tone = InnerTwelveToneInterpretation.C

        repeat(InnerTwelveToneInterpretation.numberOfTones){
            tone.nextTone()
        }

        assertEquals(tone, InnerTwelveToneInterpretation.C){
            "The last tone should be C"
        }
    }

    @Test
    fun previousTone() {
        var tone = InnerTwelveToneInterpretation.C
        repeat(InnerTwelveToneInterpretation.numberOfTones){
            tone.previousTone()
        }

        assertEquals(tone, InnerTwelveToneInterpretation.C){
            "The last tone should be C"
        }
    }
}
