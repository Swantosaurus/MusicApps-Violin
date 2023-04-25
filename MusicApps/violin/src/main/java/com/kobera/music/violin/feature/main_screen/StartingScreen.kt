package com.kobera.music.violin.feature.main_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.ui.util.setSystemBarColors
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.NavGraphs
import com.kobera.music.violin.feature.destinations.DirectionDestination
import com.kobera.music.violin.feature.destinations.RecognizeNoteScreenDestination
import com.kobera.music.violin.feature.destinations.TunerScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@Destination
@RootNavGraph(start = true)
@Composable
fun StartingScreen(startingScreenViewModel: StartingScreenViewModel = getViewModel(), navigator: DestinationsNavigator) {
    val scoreState by startingScreenViewModel.score.collectAsStateWithLifecycle()
    StartingScreenBody(navigator = navigator, scoreState = scoreState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartingScreenBody(navigator: DestinationsNavigator?, scoreState: ScoreState) {
    Scaffold() { paddingValues ->
        Column(Modifier.padding(paddingValues = paddingValues)){
            ScoreStats(scoreState = scoreState)
            StartingScreenBody(modifier = Modifier.padding(paddingValues), navigator = navigator)
        }
    }
}

@Composable
fun ScoreStats(scoreState: ScoreState) {
    when(scoreState){
        is ScoreState.Loading -> {
            Text(text = "Loading")
        }
        is ScoreState.Success -> {
            Text(text = "Score: ${scoreState.score}")
        }
        is ScoreState.Error -> {
            Text(text = "Error: ${scoreState.error.message}")
        }
    }
}

@Composable
fun StartingScreenBody(modifier: Modifier = Modifier, navigator: DestinationsNavigator?) {
    setSystemBarColors()
    Column(modifier = modifier) {
        Stats()
        Navigation(navigator = navigator)
    }
}


@Composable
private fun Stats() {
    //TODO
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

data class NavigationDescription(val icon: Painter, val title: String)

@Composable
private fun getUiDescriptionForDestination(navigationDestination: com.kobera.music.violin.feature.destinations.Destination): NavigationDescription? {
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

        else -> {
            null//throw IllegalStateException("Undefined destination UI")
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
fun StartingScreenPreview(){
    StartingScreenBody(navigator = null)
}