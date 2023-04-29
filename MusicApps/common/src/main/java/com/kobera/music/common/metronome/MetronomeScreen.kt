package com.kobera.music.common.metronome

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.R
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.kobera.music.common.util.filterNonDigits
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun MetronomeScreen(
    navigator: DestinationsNavigator,
    viewModel: MetronomeViewModel = getViewModel()
) {
    val tickState by viewModel.tickState.collectAsStateWithLifecycle()
    val metronomeSpeed by viewModel.speed.collectAsStateWithLifecycle()
    MetronomeScreenBody(
        tickState = tickState,
        navigator = navigator,
        start = {
            viewModel.startMetronome()
        },
        stop = {
            viewModel.stopMetronome()
        },
        metronomeSpeed = metronomeSpeed,
        metronomeSpeedChange = {
            viewModel.changeSpeedOfMetronome(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
fun MetronomeScreenBody(
    tickState: MetronomeState,
    metronomeSpeed: Int,
    metronomeSpeedChange: (Int) -> Unit,
    navigator: DestinationsNavigator?,
    start: () -> Unit,
    stop: () -> Unit
) {
    Scaffold(Modifier.fillMaxSize()) {
        MetronomeBackground(tickState = tickState)
        Box(modifier = Modifier.padding(it)) {
            MetronomeScreenTopBar(navigator = navigator)
        }
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MetronomeControls(
                tickState = tickState,
                start, stop,
                metronomeSpeedChange,
                metronomeSpeed,
            )
        }
    }
}

@Composable
private fun MetronomeControls(
    tickState: MetronomeState,
    start: () -> Unit,
    stop: () -> Unit,
    metronomeSpeedChange: (Int) -> Unit,
    metronomeSpeed: Int
) {
    Text(text = stringResource(R.string.beats_per_second))
    Row(){
        OutlinedIconButton(onClick = { metronomeSpeedChange(metronomeSpeed - 1) }) {
            Icon(painter = painterResource(id = R.drawable.baseline_remove_24), contentDescription = null)
        }
            

        BasicTextField(
            value = metronomeSpeed.toString(),
            onValueChange = {
                metronomeSpeedChange(it.filterNonDigits().toIntOrNull() ?: 0)
            }
        )
    }

    
    OutlinedIconButton(
        onClick = {
            if (tickState is MetronomeState.Running) {
                stop()
            } else {
                start()
            }
        },
        Modifier.size(100.dp),
        border = BorderStroke(5.dp, MaterialTheme.colorScheme.onSurface),
    ) {
        Icon(
            painter = painterResource(
                id = if (tickState is MetronomeState.Running) R.drawable.round_stop_24
                    else R.drawable.round_play_arrow_24
            ),
            modifier = Modifier.size(50.dp),
            contentDescription = stringResource(R.string.start_stop)
        )
    }
}


@Composable
private fun MetronomeScreenTopBar(navigator: DestinationsNavigator?) {
    CenteredNavigationBarWithNavigateBack(
        navigator = navigator,
        label = stringResource(R.string.metronome)
    )
}

@Composable
private fun MetronomeBackground(tickState: MetronomeState) {
    val animatedColor by animateColorAsState(
        targetValue = when (tickState) {
            is MetronomeState.PrimaryTick -> MaterialTheme.colorScheme.primary
            is MetronomeState.SecondaryTick -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.surface
        },


        animationSpec = @Suppress("MagicNumber") tween(30)
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = animatedColor
    ) {}
}
