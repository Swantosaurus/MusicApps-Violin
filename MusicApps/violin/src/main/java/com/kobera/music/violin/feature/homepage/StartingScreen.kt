@file:Suppress("TooManyFunctions")
package com.kobera.music.violin.feature.homepage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.score.ScoreRepository
import com.kobera.music.common.ui.component.AccuracyGraph
import com.kobera.music.common.ui.util.setSystemBarColors
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.NavGraphs
import com.kobera.music.violin.feature.destinations.DirectionDestination
import com.kobera.music.violin.feature.destinations.MetronomeWrapperDestination
import com.kobera.music.violin.feature.destinations.RecognizeNoteScreenDestination
import com.kobera.music.violin.feature.destinations.TunerScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@Destination
@RootNavGraph(start = true)
@Composable
fun StartingScreen(
    startingScreenViewModel: StartingScreenViewModel = getViewModel(),
    navigator: DestinationsNavigator
) {
    val totalScoreState by startingScreenViewModel.totalScore.collectAsStateWithLifecycle()
    val totalWinsAndLoses by startingScreenViewModel.totalWinsAndLoses.collectAsStateWithLifecycle()
    val todayScoreState by startingScreenViewModel.scoreToday.collectAsStateWithLifecycle()
    val todayWinsAndLoses by startingScreenViewModel.winsAndLosesToday.collectAsStateWithLifecycle()

    setSystemBarColors()

    StartingScreenBody(
        navigator = navigator,
        totalScoreState = totalScoreState,
        totalWinsAndLoses = totalWinsAndLoses,
        todayScoreState = todayScoreState,
        todayWinsAndLoeses = todayWinsAndLoses
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartingScreenBody(
    navigator: DestinationsNavigator?,
    totalScoreState: ScoreState,
    totalWinsAndLoses: WinsAndLosesState,
    todayScoreState: ScoreState,
    todayWinsAndLoeses: WinsAndLosesState
) {
    Scaffold() { paddingValues ->
        Column(Modifier.padding(paddingValues = paddingValues)) {
            ScoreStats(
                totalScoreState = totalScoreState,
                totalWinsAndLoses = totalWinsAndLoses,
                todayScoreState = todayScoreState,
                todayWinsAndLoeses = todayWinsAndLoeses
            )

            Navigation(navigator = navigator)
        }
    }
}

@Composable
private fun ScoreStats(
    totalScoreState: ScoreState,
    totalWinsAndLoses: WinsAndLosesState,
    todayScoreState: ScoreState,
    todayWinsAndLoeses: WinsAndLosesState
) {
    Row() {
        Column(Modifier.weight(1f).padding(10.dp)) {
            ScoreValue(totalScore = totalScoreState, scoreLabel = stringResource(R.string.total))
            Accuracy(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(50.dp)
                    .fillMaxWidth(),
                todaysWinsAndLosesState = totalWinsAndLoses
            )
        }
        Column(Modifier.weight(1f).padding(10.dp)) {
            ScoreValue(totalScore = todayScoreState, scoreLabel = stringResource(R.string.today))
            Accuracy(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(50.dp)
                    .fillMaxWidth(),
                todaysWinsAndLosesState = todayWinsAndLoeses
            )
        }
    }
}

@Composable
private fun ScoreValue(totalScore: ScoreState, scoreLabel: String) {
    when (totalScore) {
        is ScoreState.Loading -> {
            Text(text = "Loading")
        }

        is ScoreState.Success -> {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = scoreLabel, style = MaterialTheme.typography.headlineLarge)

                Text(text = "${totalScore.score}", style = MaterialTheme.typography.headlineMedium)
            }
        }

        is ScoreState.Error -> {
            Text(text = "Error: ${totalScore.error.message}")
        }
    }
}

@Composable
private fun Accuracy(modifier: Modifier, todaysWinsAndLosesState: WinsAndLosesState) {
    Box(modifier = modifier) {
        when (todaysWinsAndLosesState) {
            is WinsAndLosesState.Loading -> {
                Text(text = "Loading")
            }

            is WinsAndLosesState.Success -> {
                Row() {
                    AccuracyGraph(
                        success = todaysWinsAndLosesState.winsAndLoses.wins,
                        fails = todaysWinsAndLosesState.winsAndLoses.loses
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.headlineSmall) {
                        Text(
                            text = stringResource(id = R.string.wins)
                                    + "${todaysWinsAndLosesState.winsAndLoses.wins}"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(R.string.fails)
                                    + "${todaysWinsAndLosesState.winsAndLoses.loses}"
                        )
                    }
                }
            }


            is WinsAndLosesState.Error -> {
                Text("Error")
            }
        }
    }
}

@Composable
private fun Navigation(navigator: DestinationsNavigator?) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(50.dp),
    ) {
        items(NavGraphs.root.destinations) { destination ->
            val uiDescription = getUiDescriptionForDestination(navigationDestination = destination)
            uiDescription?.let {
                NavigationItem(icon = uiDescription.icon, title = uiDescription.title) {
                    navigator?.navigate(destination as DirectionDestination)
                }
            }
        }
    }
}

/**
 * Navigation item UI description for the starting screen
 *
 * @param icon - icon for the navigation item
 * @param title - title for the navigation item
 */
data class NavigationDescription(val icon: Painter, val title: String)

@Composable
private fun getUiDescriptionForDestination(
    navigationDestination: com.kobera.music.violin.feature.destinations.Destination
): NavigationDescription? {
    return when (navigationDestination) {
        is RecognizeNoteScreenDestination -> {
            NavigationDescription(
                painterResource(id = R.drawable.ic_violin),
                stringResource(R.string.recognize_note)
            )
        }

        is TunerScreenDestination -> {
            NavigationDescription(
                painterResource(id = R.drawable.ic_violin),
                stringResource(R.string.tuner)
            )
        }

        is MetronomeWrapperDestination -> {
            NavigationDescription(
                painterResource(id = R.drawable.ic_violin),
                stringResource(com.kobera.music.common.R.string.metronome)
            )
        }

        else -> {
            null
        }
    }
}

@Composable
fun NavigationItem(icon: Painter, title: String, navigate: () -> Unit) {
    Column(modifier = Modifier.clickable {
        navigate()
    }, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = icon, contentDescription = null)
        Text(text = title)
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    showSystemUi = true,
    name = "Starting Screen"
)
@Composable
fun StartingScreenPreview() {
    StartingScreenBody(
        navigator = null,
        ScoreState.Success(1000),
        WinsAndLosesState.Success(ScoreRepository.WinsAndLoses(10, 5)),
        ScoreState.Success(100),
        WinsAndLosesState.Success(ScoreRepository.WinsAndLoses(10, 5))
    )
}
