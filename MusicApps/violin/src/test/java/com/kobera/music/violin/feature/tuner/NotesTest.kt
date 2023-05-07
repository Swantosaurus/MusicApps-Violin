package com.kobera.music.violin.feature.tuner

import com.kobera.music.common.notes.Tones
import com.kobera.music.common.notes.TwelveToneNames
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.pow


internal class NotesTest {

    val doubleDelta = 0.000001
    @Test
    fun getNotes() {
        TwelveToneNames.setNamesFromLowestToHighest(
            arrayOf(
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
                "B",
            )
        )
        val notes = Tones.getTones(440.0)


        assertTrue(abs(220.0- notes["A3"]!!.frequency)< doubleDelta){
            "A3 should be 220.0 but was ${notes["A3"]!!.frequency}"
        }

        assertTrue(notes["A3"]!!.isInTune(220.0))
        assertTrue(notes["A3"]!!.isInTune(220.0 * 2.0.pow(1.0 / 240.0)))
        assertFalse(notes["A3"]!!.isInTune(220.0 / 2.0.pow(1.0/12.0)))
        assertFalse(notes["A3"]!!.isInRange(220.0 * 2.0.pow(1.0/12.0)))
        assertTrue(notes["A3"]!!.isInRange(220.0 * 2.0.pow(1.0/25.0)))

    }
}
