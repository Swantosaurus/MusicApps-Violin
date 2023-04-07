package com.kobera.music.violin.feature.recognize_note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.*
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun RecognizeNoteScreen() {
    RecognizeNoteScreenBody()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeNoteScreenBody() {
    Scaffold() { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Sheet(
                modifier = Modifier,
                notes = listOf(
                    SheetNote(D, 4),
                    SheetNote(E, 4),
                    SheetNote(F, 4),
                    SheetNote(G, 4),
                    SheetNote(A, 4),
                    SheetNote(B, 4),
                    SheetNote(C, 5),
                    SheetNote(D, 5),
                    SheetNote(C, 5),
                    SheetNote(B, 4),
                    SheetNote(A, 4),
                    SheetNote(G, 4),
                    SheetNote(F, 4),
                    SheetNote(E, 4),
                    SheetNote(D, 4),
                ),
                keySignature = KeySignature.Sharps(2)
            )
        }
    }
}