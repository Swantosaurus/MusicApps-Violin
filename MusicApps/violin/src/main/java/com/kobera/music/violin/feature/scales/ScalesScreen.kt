package com.kobera.music.violin.feature.scales

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import com.kobera.music.common.notes.scale.Scale
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBackAndRightActionButton
import com.kobera.music.violin.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun ScalesScreen(viewModel: ScalesViewModel = getViewModel(), navigator: DestinationsNavigator) {
    val selectedScale by viewModel.scaleState.collectAsStateWithLifecycle()
    when (selectedScale) {
        is ScaleState.Loading -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

        is ScaleState.Ready -> {
            ScalesScreenDrawer(
                viewModel = viewModel,
                selectedScale = (selectedScale as ScaleState.Ready).scale,
                navigator = navigator
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalesScreenDrawer(
    viewModel: ScalesViewModel?,
    selectedScale: Scale,
    navigator: DestinationsNavigator
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val  openDrawer : () -> Unit = {
        coroutineScope.launch {
            drawerState.open()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                @Suppress("MagicNumber")
                Modifier.width(240.dp + 16.dp * 3),
                windowInsets = DrawerDefaults.windowInsets.add((WindowInsets(left = 16.dp)))
            ) {
                DrawerContent(
                    action = { selected, scale ->
                        if (selected) {
                            viewModel?.changeScale(scale = scale)
                        }
                    },
                    selectedScale = selectedScale
                )
            }
        }
    ) {
        ScalesScreenBody(
            selectedScale = selectedScale,
            navigator = navigator,
            openDrawer = openDrawer
        )
    }
}

@Composable
private fun DrawerContent(action: (Boolean, Scale) -> Unit, selectedScale: Scale) {
    Row(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.major_scales),
                style = MaterialTheme.typography.headlineMedium
            )
            MajorScale.values().forEach {
                ScaleToggleButtton(
                    action = action,
                    selectedScale = selectedScale,
                    scale = it
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.minor),
                style = MaterialTheme.typography.headlineMedium
            )
            MinorScale.values().forEach {
                ScaleToggleButtton(
                    action = action,
                    selectedScale = selectedScale,
                    scale = it
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun ScaleToggleButtton(
    action: (Boolean, Scale) -> Unit,
    selectedScale: Scale,
    scale: Scale
) {
    FilledIconToggleButton(
        modifier = Modifier.width(120.dp),
        checked = selectedScale == scale,
        onCheckedChange = {
            action(it, scale)
        }) {
        Text(text = scale.name)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalesScreenBody(
    selectedScale: Scale,
    navigator: DestinationsNavigator,
    openDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val color by animateColorAsState(
                targetValue = if (!scrollState.canScrollBackward) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.background
            )
            CenteredNavigationBarWithNavigateBackAndRightActionButton(
                modifier = Modifier.background(color).systemBarsPadding().fillMaxWidth(),
                navigator = navigator,
                text = "Scales",
                rightActionButtonPainter = rememberVectorPainter(image = Icons.Default.Settings)
            ) {
                openDrawer()
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            val notes = selectedScale.getNotes()
            Sheet(notes = notes + notes.reversed(), keySignature = selectedScale.getKeySignature())
        }
    }
}
