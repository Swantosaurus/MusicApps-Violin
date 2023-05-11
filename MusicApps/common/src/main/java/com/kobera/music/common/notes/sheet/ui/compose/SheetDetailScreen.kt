package com.kobera.music.common.notes.sheet.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
fun SheetDetailScreen(
    viewModel: ScaleDetailViewModel = ScaleDetailViewModel(),
    navigator: DestinationsNavigator,
    songName: String
) {
    val scoreState by viewModel.scoreState.collectAsStateWithLifecycle()
    //viewModel.setScore(notes)
    SheetDetailBody(scoreState, navigator, songName)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDetailBody(scoreState: ScoreState, navigator: DestinationsNavigator?, songName: String) {
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
                    .statusBarsPadding(), navigator = navigator, label = songName
            )
        }
    ) {
        when(scoreState){
            is ScoreState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
            is ScoreState.Success -> {
                Sheet(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(scrollState), notes = scoreState.score, keySignature = scoreState.keySignature
                )
            }
            else -> TODO()
        }
    }
}

