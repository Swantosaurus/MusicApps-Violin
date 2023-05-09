package com.kobera.music.common.notes.scale

import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MajorScaleTest {
    @Suppress("NestedBlockDepth")
    @Test
    fun values() {
        MajorScale.values().forEach { scale ->
            when(scale.getKeySignature()){

                is KeySignature.Flats -> {
                    var numberOfSharps = 0
                    var numberOfFlats = 0

                    scale.getNotes().forEach {
                        when(it.noteParams.accidental){
                            SheetNote.Params.Accidental.Flat -> numberOfFlats++
                            SheetNote.Params.Accidental.Sharp -> numberOfSharps++
                            SheetNote.Params.Accidental.None -> {}
                            else -> error("Unknown accidental")
                        }
                    }
                    assertEquals(numberOfSharps, 0){
                        "${scale.name} has a sharp even though it should have flats"
                    }
                    assertEquals(numberOfFlats, (scale.getKeySignature() as KeySignature.Flats).numberOfFlats){
                        "${scale.name} has wrong number of Flats"
                    }
                }
                is KeySignature.Sharps ->{
                    var numebrOfSharps = 0
                    var numberOfFlats = 0
                    scale.getNotes().forEach {
                        when(it.noteParams.accidental){
                            SheetNote.Params.Accidental.Flat -> numberOfFlats++
                            SheetNote.Params.Accidental.Sharp -> numebrOfSharps++
                            SheetNote.Params.Accidental.None -> {}
                            else -> error("Unknown accidental")
                        }
                    }
                    assertEquals(numebrOfSharps, (scale.getKeySignature() as KeySignature.Sharps).numberOfSharps){
                        "${scale.name} has wrong number of Sharps"
                    }
                    assertEquals(numberOfFlats, 0){
                        "${scale.name} has a flat even though it should have sharps"
                    }
                }
            }
        }
    }
}
