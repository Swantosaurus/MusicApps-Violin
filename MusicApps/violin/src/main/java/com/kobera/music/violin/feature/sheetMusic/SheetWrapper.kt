package com.kobera.music.violin.feature.sheetMusic

import androidx.compose.runtime.Composable
import com.kobera.music.common.notes.sheet.ui.compose.SheetDetailScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator



@Destination
@Composable
fun SheetWrapper(
    navigator: DestinationsNavigator,
    songName: String,
) {
    SheetDetailScreen(navigator = navigator, songName = songName)
}
