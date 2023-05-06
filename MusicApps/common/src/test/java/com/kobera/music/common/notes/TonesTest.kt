package com.kobera.music.common.notes

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TonesTest {

   @BeforeAll
    fun setup() {
        TwelveToneNames.setNamesFromLowestToHighestVarArg(
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
    }


    @Test
    fun getTones() {
        val tones = Tones.getTones(Tones.defaultA4Frequency)

        assertEquals(tones.size, 12 * 6){
            "There should be 72 tones"
        }
    }
}
