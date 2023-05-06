package com.kobera.music.violin.feature.sheetMusic

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.destinations.ScalesScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SheetMusicListScreen (navigator: DestinationsNavigator){
    SheetMusicListScreenBody(navigator = navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetMusicListScreenBody(navigator: DestinationsNavigator?) {
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            val color by animateColorAsState(
                targetValue = if (!lazyListState.canScrollBackward) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surfaceVariant
            )

            Box(modifier = Modifier.systemBarsPadding()) {
                CenteredNavigationBarWithNavigateBack(
                    modifier = Modifier.fillMaxWidth().systemBarsPadding().background(color = color),
                    navigator = navigator,
                    label = stringResource(id = R.string.sheets)
                )
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(state = lazyListState){
                item {
                    ListItem(title = stringResource(R.string.scales)){
                        navigator?.navigate(ScalesScreenDestination)
                    }
                }
            }
        }
    }

}

@Composable
private fun ListItem(title: String, action: () -> Unit) {
    Row(
        Modifier
            .height(56.dp)
            .fillMaxWidth()
            .clickable(onClick = action)
    ) {
        Text(text = title)
    }
}
