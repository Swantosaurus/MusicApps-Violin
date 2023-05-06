@file:Suppress("TooManyFunctions")
package com.kobera.music.violin.feature.homepage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.score.ScoreRepository
import com.kobera.music.common.ui.component.AccuracyGraph
import com.kobera.music.common.ui.util.lockScreenOrientation
import com.kobera.music.common.ui.util.setSystemBarColors
import com.kobera.music.common.util.toStringWithNDecimals
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.destinations.DirectionDestination
import com.kobera.music.violin.feature.destinations.MetronomeWrapperDestination
import com.kobera.music.violin.feature.destinations.RecognizeNoteScreenDestination
import com.kobera.music.violin.feature.destinations.SheetMusicListScreenDestination
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

    lockScreenOrientation()

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
        Column(
            Modifier
                .padding(paddingValues = paddingValues)
                //.verticalScroll(rememberScrollState())
        ) {
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
        Column(
            Modifier
                .weight(1f)
                .padding(10.dp)) {
            ScoreValue(totalScore = totalScoreState, scoreLabel = stringResource(R.string.total))
            Accuracy(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .height(50.dp)
                    .fillMaxWidth(),
                todaysWinsAndLosesState = totalWinsAndLoses
            )
        }
        Column(
            Modifier
                .weight(1f)
                .padding(10.dp)) {
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
                    ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                        Text(
                            text = stringResource(id = R.string.wins)
                                    + "${todaysWinsAndLosesState.winsAndLoses.wins}"
                        )
                        Text(
                            text = "${todaysWinsAndLosesState.successRate.toStringWithNDecimals(1)} %",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
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
    val sections  = getSections()

    LazyColumn(
        Modifier
            .padding(10.dp),
    ) {
        items(sections) { section ->
            Text(text = section.title, style = MaterialTheme.typography.headlineMedium)
            LazyRow(
                Modifier
                    .padding(10.dp)
            ) {
                items(section.destinations) { destination ->
                    NavigationItem(
                        icon = destination.icon,
                        title = destination.title,
                        navigate = { navigator?.navigate(destination.destination) }
                    )
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
data class NavigationDescription(val icon: Painter, val title: String, val destination: DirectionDestination)

/**
 *  Section of the navigation
 *
 *  @param title - title of the section
 *  @param destinations - list of navigation items in the section
 */
data class NavigationSection(val title: String, val destinations: List<NavigationDescription>)

@Composable
private fun getSections(): List<NavigationSection> =
    listOf(
        NavigationSection(
            stringResource(R.string.games),
            listOf(
                NavigationDescription(
                    icon = painterResource(id = R.drawable.baseline_question_mark_24),
                    title = stringResource(id = R.string.recognize_note),
                    destination = RecognizeNoteScreenDestination
                ),
                NavigationDescription(
                    icon = painterResource(id = R.drawable.ic_violin),
                    title = stringResource(R.string.songs),
                    destination = SheetMusicListScreenDestination
                )
            )
        ),
        NavigationSection(
            title = "Utilities",
            destinations = listOf(
                NavigationDescription(
                    icon = painterResource(id = com.kobera.music.common.R.drawable.tuning_pegs),
                    title = stringResource(R.string.tuner),
                    destination = TunerScreenDestination
                ),
                NavigationDescription(
                    icon = painterResource(id = com.kobera.music.common.R.drawable.metronome),
                    title = stringResource(com.kobera.music.common.R.string.metronome),
                    destination = MetronomeWrapperDestination
                )
            )
        )
    )


@Composable
fun NavigationItem(icon: Painter, title: String, navigate: () -> Unit) {
    Column(modifier = Modifier.clickable {
        navigate()
    }, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(modifier = Modifier.size(100.dp), painter = icon, contentDescription = null)
        Text(text = title, style = MaterialTheme.typography.labelLarge)
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
