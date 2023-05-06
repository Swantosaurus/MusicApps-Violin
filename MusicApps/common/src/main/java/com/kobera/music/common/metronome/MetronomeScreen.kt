package com.kobera.music.common.metronome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.R
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.kobera.music.common.ui.util.lockScreenOrientation
import com.kobera.music.common.ui.util.withLifecycle
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
    val numberOfBeats by viewModel.numberOfTicks.collectAsStateWithLifecycle()

    lockScreenOrientation()

    withLifecycle(
        onStop = {
            viewModel.stopMetronome()
        }
    )

    MetronomeScreenBody(
        tickState = { tickState },
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
        },
        numberOfBeats = numberOfBeats,
        numberOfBeatsChange = {
            viewModel.changeNumberOfTicks(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
fun MetronomeScreenBody(
    tickState: () -> MetronomeState,
    metronomeSpeed: Int,
    metronomeSpeedChange: (Int) -> Unit,
    numberOfBeats: Int,
    numberOfBeatsChange: (Int) -> Unit,
    navigator: DestinationsNavigator?,
    start: () -> Unit,
    stop: () -> Unit
) {
    Scaffold(Modifier.fillMaxSize(),
        topBar = {
            MetronomeScreenTopBar(navigator = navigator)
        }
    ) {
        MetronomeBackground(tickStateLambda = tickState)
        Column(
            Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            MetronomeControls(
                tickState = tickState,
                start = start,
                stop = stop,
                metronomeSpeedChange = metronomeSpeedChange,
                metronomeSpeed = metronomeSpeed,
                numberOfBeats = numberOfBeats,
                numberOfBeatsChange = numberOfBeatsChange
            )
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
private fun MetronomeControls(
    tickState: () -> MetronomeState,
    start: () -> Unit,
    stop: () -> Unit,
    metronomeSpeedChange: (Int) -> Unit,
    metronomeSpeed: Int,
    numberOfBeats: Int,
    numberOfBeatsChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NumberOfBeatsSetting(
            numberOfBeats = numberOfBeats,
            changeNumberOfBeats = numberOfBeatsChange
        )
        Spacer(Modifier.height(40.dp))
        BpmSetting(metronomeSpeed, metronomeSpeedChange)
        Spacer(Modifier.height(40.dp))
        StartStopButton(start = start, stop = stop, tickState = tickState)
    }
}

@Composable
fun StartStopButton(start: () -> Unit, stop: () -> Unit, tickState: () -> MetronomeState) {
    OutlinedIconButton(
        onClick = {
            if (tickState() is MetronomeState.Running) {
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
                id = if (tickState() is MetronomeState.Running) R.drawable.round_stop_24
                else R.drawable.round_play_arrow_24
            ),
            modifier = Modifier.size(50.dp),
            contentDescription = stringResource(R.string.start_stop)
        )
    }
}

@Composable
private fun NumberOfBeatsSetting(numberOfBeats: Int, changeNumberOfBeats: (Int) -> Unit) {
    Text(
        text = stringResource(R.string.ticks),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChangeButton(
            changeValue = {
                changeNumberOfBeats(numberOfBeats - 1)
            },
            painter = painterResource(id = R.drawable.baseline_remove_24)
        )

        Text(
            text = numberOfBeats.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.width(50.dp)
        )

        ChangeButton(
            changeValue = {
                changeNumberOfBeats(numberOfBeats + 1)
            },
            painter = painterResource(id = R.drawable.baseline_add_24)
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun BpmSetting(metronomeSpeed: Int, metronomeSpeedChange: (Int) -> Unit) {
    //copy of state so TextField doesnt update on every change
    // -> user can better work with it (when directly modifiing state it worked wierdy with its cap)
    var metronomeSpeedValue by remember {
        mutableStateOf(metronomeSpeed.toString())
    }
    Text(
        text = stringResource(R.string.beats_per_second),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChangeButton(
            changeValue = {
                metronomeSpeedChange(metronomeSpeed - 1)
                metronomeSpeedValue = (metronomeSpeedValue.toInt() - 1).let {
                    return@let when (true) {
                        (it < MetronomeViewModel.MIN_SPEED) -> MetronomeViewModel.MIN_SPEED
                        (it > MetronomeViewModel.MAX_SPEED) -> MetronomeViewModel.MAX_SPEED
                        else -> it
                    }
                }.toString()
            },
            painter = painterResource(id = R.drawable.baseline_remove_24)
        )
        val lfm = LocalFocusManager.current

        @Suppress("MagicNumber")
        val maxLength = 3

        BasicTextField(
            value = metronomeSpeedValue,
            onValueChange = {
                metronomeSpeedValue = it.filterNonDigits().take(maxLength)
            },
            modifier = Modifier.width(50.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),

            keyboardActions = KeyboardActions(
                onDone = {
                    metronomeSpeedValue = metronomeSpeedValue.filterNonDigits().take(maxLength).let {
                        frequencyFilter(it.toIntOrNull() ?: MetronomeViewModel.MIN_SPEED)
                    }.toString()
                    metronomeSpeedChange(
                        metronomeSpeedValue.filterNonDigits().toInt()
                    )

                    lfm.clearFocus()
                }
            ),
        )

        ChangeButton(
            changeValue = {
                metronomeSpeedChange(metronomeSpeed + 1)
                metronomeSpeedValue = (metronomeSpeedValue.toInt() + 1).let {
                    frequencyFilter(it)
                }.toString()
            },
            painter = painterResource(id = R.drawable.baseline_add_24)
        )
    }
}

fun frequencyFilter(it: Int): Int =
    when (true) {
        (it < MetronomeViewModel.MIN_SPEED) -> MetronomeViewModel.MIN_SPEED
        (it > MetronomeViewModel.MAX_SPEED) -> MetronomeViewModel.MAX_SPEED
        else -> it
    }

@Composable
fun ChangeButton(changeValue: () -> Unit, painter: Painter) {
    Button(
        modifier = Modifier.size(48.dp),
        onClick = {
            changeValue()
        },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            painter = painter,
            contentDescription = null
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
private fun MetronomeBackground(tickStateLambda: () -> MetronomeState) {
    val tickState = tickStateLambda()
   /* val animatedColor by animateColorAsState(
        targetValue = when (tickState) {
            is MetronomeState.PrimaryTick -> MaterialTheme.colorScheme.primary
            is MetronomeState.SecondaryTick -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.surface
        },


        animationSpec = @Suppress("MagicNumber") tween(30)
    )*/
    Surface(
        modifier = Modifier.fillMaxSize(),
        color =  when (tickState) {
            is MetronomeState.PrimaryTick -> MaterialTheme.colorScheme.primary
            is MetronomeState.SecondaryTick -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.surface
        }
    ) {}
}
