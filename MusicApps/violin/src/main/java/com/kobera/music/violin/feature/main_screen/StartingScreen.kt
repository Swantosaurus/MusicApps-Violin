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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.NavGraphs
import com.kobera.music.violin.feature.destinations.DirectionDestination
import com.kobera.music.violin.feature.destinations.RecognizeNoteScreenDestination
import com.kobera.music.violin.feature.destinations.TunerScreenDestination
import com.kobera.music.violin.utils.setSystemBarColors
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination(start = true)
@Composable
fun StartingScreen(navigator: DestinationsNavigator) {
    Scaffold() { paddingValues ->
        StartingScreenBody(modifier = Modifier.padding(paddingValues), navigator = navigator)
    }

}

@Composable
fun StartingScreenBody(modifier: Modifier = Modifier, navigator: DestinationsNavigator?) {
    setSystemBarColors()
    Column(modifier = modifier) {
        Graphs()
        Navigation(navigator = navigator)
    }
}


@Composable
private fun Graphs() {

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