package com.kobera.music.violin.feature.sheetMusic

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun SheetTest(navigator: DestinationsNavigator) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val color by animateColorAsState(
                targetValue = if (!scrollState.canScrollBackward) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
            )
            CenteredNavigationBarWithNavigateBack(
                modifier = Modifier
                    .background(color)
                    .fillMaxWidth()
                    .statusBarsPadding(), navigator = navigator, label = "Test"
            )
        }
    ) {
        val notes: MutableList<SheetNote> = mutableListOf(
            SheetNote.A.copy(octave = 5, noteParams = SheetNote.Params(
                SheetNote.Params.Duration.Eighth
            )),
        )
        /*for (scale in MinorScale.values()) {
            notes += scale.getNotes()
        }
        for (scale in MajorScale.values()) {
            notes += scale.getNotes()
        }*/
        Sheet(
            modifier = Modifier
                .padding(it)
                .verticalScroll(scrollState), notes = notes, keySignature = KeySignature.None
        )
    }
}
