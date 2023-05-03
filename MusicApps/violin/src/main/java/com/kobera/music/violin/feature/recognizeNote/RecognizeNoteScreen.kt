@file:Suppress("TooManyFunctions")
package com.kobera.music.violin.feature.recognizeNote

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.*
import com.kobera.music.common.notes.scale.MajorScale
import com.kobera.music.common.notes.scale.MinorScale
import com.kobera.music.common.notes.sheet.InnerSheetNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBackAndRightActionButton
import com.kobera.music.common.ui.component.HandleAudioPermission
import com.kobera.music.common.ui.component.SensitivitySetting
import com.kobera.music.common.ui.util.withLifecycle
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.fingerboardInput.FingerboardInputView
import com.kobera.music.violin.feature.recognizeNote.model.RecognizeNoteScales
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun RecognizeNoteScreen(
    viewModel: RecognizeNoteViewModel = getViewModel(),
    navigator: DestinationsNavigator,
) {
    val generatedNoteState by viewModel.generatedNote.collectAsStateWithLifecycle()
    val sensitivity by viewModel.sensitivity.collectAsStateWithLifecycle()
    val recognizeNoteState by viewModel.recognizeNoteState.collectAsStateWithLifecycle()
    val scales by viewModel.scales.collectAsStateWithLifecycle()
    val microphoneEnabled by viewModel.microphoneEnabled.collectAsStateWithLifecycle()


    var alertVisible by remember { mutableStateOf(false) }
    val showDialog = {
        alertVisible = true
    }
    val hideVisible = {
        alertVisible = false
    }

    HandleAudioPermission(
        permissionGranted = {
            withLifecycle(
                onStart = {
                    viewModel.startListeningFrequencies()
                },
                onStop = {
                    viewModel.stopListeningResponses()
                }
            )
        },
        showRationale = {
            Rationale(it)
        },
        permissionDenied = {}
    )
    OpenSettingsDialog(alertVisible, hideVisible)
    RecognizeScreenDrawer(
        viewModel = viewModel,
        generatedNoteState = { generatedNoteState },
        sensitivity = { sensitivity },
        recognizeNoteState = { recognizeNoteState },
        navigator = navigator,
        microphoneEnabled = { microphoneEnabled },
        scales = {
            when (scales) {
                is RecognizeNoteScaleState.Ready -> (scales as RecognizeNoteScaleState.Ready).scales
                else -> RecognizeNoteScales()
            }
        },
        showDialog = showDialog
    )

}

@Composable
fun OpenSettingsDialog(visible: Boolean, hide: () -> Unit) {
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    if (visible) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.alert_game_wo_audio_title)) },
            text = { Text(text = stringResource(R.string.alert_game_wo_audio)) },
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { context.startActivity(intent) }) {
                    Text(text = stringResource(id = R.string.open_settings))
                }
            }, dismissButton = {
                TextButton(onClick = hide) {
                    Text(text = stringResource(R.string.denny))
                }
            })
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Rationale(permissionState: PermissionState) {
    var shown by remember { mutableStateOf(true) }
    if (shown) {
        AlertDialog(
            title = { Text(text = stringResource(R.string.alert_game_wo_audio_title)) },
            text = { Text(text = stringResource(R.string.alert_game_wo_audio)) },
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { permissionState.launchPermissionRequest() }) {
                    Text(text = stringResource(R.string.allow))
                }
            }, dismissButton = {
                TextButton(onClick = { shown = false }) {
                    Text(text = stringResource(R.string.denny))
                }
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeScreenDrawer(
    viewModel: RecognizeNoteViewModel?,
    generatedNoteState: () -> GeneratedNoteState,
    sensitivity: () -> Float,
    recognizeNoteState: () -> RecognizeNoteState,
    microphoneEnabled: () -> Boolean,
    navigator: DestinationsNavigator?,
    scales: () -> RecognizeNoteScales,
    showDialog: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                @Suppress("MagicNumber")
                Modifier.width(240.dp + 16.dp * 3),
                windowInsets = DrawerDefaults.windowInsets.add((WindowInsets(left = 16.dp)))

            ) {
                DrawerContent(viewModel = viewModel, scales = scales)
            }
        },
        drawerState = drawerState
    ) {
        RecognizeNoteScreenBody(
            viewModel = viewModel,
            generatedNoteState = generatedNoteState(),
            sensitivity = sensitivity,
            recognizeNoteState = recognizeNoteState,
            navigator = navigator,
            microphoneEnabled = microphoneEnabled,
            openDrawer = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            showDialog = showDialog
        )
    }
}


@Composable
private fun DrawerContent(viewModel: RecognizeNoteViewModel?, scales: () -> RecognizeNoteScales) {
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
                ScaleToggleButtton(viewModel = viewModel, scales = scales, scale = it)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.minor),
                style = MaterialTheme.typography.headlineMedium
            )
            MinorScale.values().forEach {
                ScaleToggleButtton(viewModel = viewModel, scales = scales, scale = it)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun ScaleToggleButtton(viewModel: RecognizeNoteViewModel?, scales: () -> RecognizeNoteScales, scale: MinorScale) {
    FilledIconToggleButton(
        modifier = Modifier.width(120.dp),
        checked = scales().minorScales.contains(scale),
        onCheckedChange = {
            if(it) {
                viewModel?.addScale(scale = scale)
            } else {
                viewModel?.removeScale(scale = scale)
            }
        }) {
        Text(text = scale.name)
    }
}

@Composable
fun ScaleToggleButtton(viewModel: RecognizeNoteViewModel?, scales: () -> RecognizeNoteScales, scale: MajorScale) {
    FilledIconToggleButton(
        modifier = Modifier.width(120.dp),
        checked = scales().majorScales.contains(scale),
        onCheckedChange = {
            if(it) {
                viewModel?.addScale(scale = scale)
            } else {
                viewModel?.removeScale(scale = scale)
            }
        }) {
        Text(text = scale.name)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeNoteScreenBody(
    viewModel: RecognizeNoteViewModel?,
    generatedNoteState: GeneratedNoteState,
    sensitivity: () -> Float,
    recognizeNoteState: () -> RecognizeNoteState,
    microphoneEnabled: () -> Boolean,
    navigator: DestinationsNavigator?,
    openDrawer: () -> Unit,
    showDialog: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FilledIconToggleButton(
                modifier = Modifier.size(48.dp),
                checked = microphoneEnabled(),
                onCheckedChange = {
                    if (viewModel?.setMicrophoneEnabled(it, context = context) == false) {
                        showDialog()
                    }
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (generatedNoteState) {
                is GeneratedNoteState.Loading -> {
                    CircularProgressIndicator()
                }

                is GeneratedNoteState.Ready -> {
                    CenteredNavigationBarWithNavigateBackAndRightActionButton(
                        navigator = navigator,
                        rightActionButtonPainter = rememberVectorPainter(image = Icons.Default.Settings),
                        text = stringResource(R.string.play_note_you_see),
                        rightActionButton = openDrawer,
                    )
                    if (viewModel != null) {
                        DisplaySheet(generatedNoteState.noteAndScale, recognizeNoteState)
                    } else {
                        SheetInPreview()
                    }
                    SensitivitySetting(sensitivity = sensitivity, setSensitivity = {
                        viewModel?.setSilenceTreashold(it)
                    })

                    Spacer(Modifier.height(50.dp))

                    FingerboardInputView(
                        scale = generatedNoteState.noteAndScale.scale
                    ) {
                        viewModel?.keyboardInput(it)
                    }
                }
                }
            }
        }
        HandleInputOverlay(
            recognizeNoteStateLambda = recognizeNoteState,
        )
    }


@Suppress("MagicNumber")
@Composable
/**
 * just for preview draws X
 */
private fun SheetInPreview() {
    Canvas(modifier = Modifier.height(200.dp)) {
        //TODO()
        drawLine(
            color = Color.Red,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(100f, 100f),
            strokeWidth = 10f
        )
        drawLine(
            color = Color.Red,
            start = androidx.compose.ui.geometry.Offset(0f, 100f),
            end = androidx.compose.ui.geometry.Offset(100f, 0f),
            strokeWidth = 10f
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun HandleInputOverlay(
    recognizeNoteStateLambda: () -> RecognizeNoteState,
) {
    val recognizeNoteState = recognizeNoteStateLambda()

    (recognizeNoteState as? RecognizeNoteState.ToShow)?.let {
        AnimatedVisibility(
            visible = it.visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            InputStatusContent(
                modifier = Modifier
                    .fillMaxSize()
                    .background(recognizeNoteState.iconAndColor.color.copy(alpha = 0.5f)),
                color = Color.White,
                painter = painterResource(id = recognizeNoteState.iconAndColor.icon),
                score = recognizeNoteState.scoreAdded
            )
        }
    }
}

@Composable
private fun InputStatusContent(
    modifier: Modifier,
    color: Color,
    painter: Painter,
    score: Int
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(10.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    Modifier.size(150.dp),
                    tint = color
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "+$score", style = MaterialTheme.typography.headlineLarge
                    .copy(fontWeight = FontWeight.Bold, fontSize = 70.sp), color = color
            )
        }
    }
}

@Composable
private fun DisplaySheet(
    noteAndKeySignature: NoteAndScale,
    recognizeNoteState: () -> RecognizeNoteState
) {
    Sheet(
        notes = listOf(
            noteAndKeySignature.note
        ),
        extraNotes = (recognizeNoteState() as? RecognizeNoteState.Wrong)?.wrongNote?.let { listOf(it) }
            ?: listOf(),
        keySignature = noteAndKeySignature.keySignature
    )
}

@Preview
@Composable
fun RecognizeNotePreview() {
    RecognizeNoteScreenBody(
        viewModel = null,
        generatedNoteState = GeneratedNoteState.Ready(
            NoteAndScale(
                note = SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    octave = 4,
                ),
                scale = MajorScale.D
            )
        ),
        sensitivity = { 0.5f },
        recognizeNoteState = { RecognizeNoteState.InTune() },
        navigator = null,
        openDrawer = {},
        showDialog = {},
        microphoneEnabled = { false }
    )
}

