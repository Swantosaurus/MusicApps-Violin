package com.kobera.music.violin.feature.metronomeWrapper

import androidx.compose.runtime.Composable
import com.kobera.music.common.metronome.MetronomeScreen
import com.kobera.music.common.metronome.MetronomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun MetronomeWrapper(navigator: DestinationsNavigator, metronomeViewModel: MetronomeViewModel= getViewModel()) {
    MetronomeScreen(navigator = navigator, viewModel = metronomeViewModel)
}