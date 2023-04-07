package com.kobera.music.violin.feature.tuner

import com.kobera.music.common.notes.Notes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.pow


internal class NotesTest {
    @Test
    fun getNotes() {
        val notes = Notes.getNotes()

        assertEquals(220.0, notes["A3"]!!.frequency)

        assertTrue(notes["A3"]!!.isInTune(220.0))
        assertTrue(notes["A3"]!!.isInTune(220.0 * 2.0.pow(1.0 / 240.0)))
        assertFalse(notes["A3"]!!.isInTune(220.0 / 2.0.pow(1.0/12.0)))
        assertFalse(notes["A3"]!!.isInRange(220.0 * 2.0.pow(1.0/12.0)))
        assertTrue(notes["A3"]!!.isInRange(220.0 * 2.0.pow(1.0/25.0)))

    }
}