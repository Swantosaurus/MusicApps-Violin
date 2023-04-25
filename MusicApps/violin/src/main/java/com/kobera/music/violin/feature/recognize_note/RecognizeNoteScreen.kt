package com.kobera.music.violin.feature.recognize_note

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.*
import com.kobera.music.common.notes.scale.getMajorScale
import com.kobera.music.common.notes.sheet.InnerSheetNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.kobera.music.common.ui.component.CenteredNavigationBarWithNavigateBack
import com.kobera.music.common.ui.component.HandleAudioPermission
import com.kobera.music.violin.R
import com.kobera.music.violin.feature.fingerboardInput.FingerboardInputView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun RecognizeNoteScreen(
    viewModel: RecognizeNoteViewModel = getViewModel(),
    navigator: DestinationsNavigator
) {
    val generatedNoteState by viewModel.generatedNote.collectAsStateWithLifecycle()
    val sensitivity by viewModel.sensitivity.collectAsStateWithLifecycle()
    val recognizeNoteState by viewModel.recognizeNoteState.collectAsStateWithLifecycle()
    HandleAudioPermission(
        permissionGranted = {
            DisposableEffect(key1 = Unit) {
                viewModel.startListeningFrequencies()
                onDispose {
                    viewModel.stopListeningResponses()
                }
            }

            RecognizeNoteScreenBody(
                viewModel = viewModel,
                generatedNoteState = generatedNoteState,
                sensitivity = { sensitivity },
                recognizeNoteState = { recognizeNoteState },
                navigator = navigator
            )
        },
        showRationale = {},
        permissionDenied = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeNoteScreenBody(
    viewModel: RecognizeNoteViewModel?,
    generatedNoteState: GeneratedNoteState,
    sensitivity: () -> Float,
    recognizeNoteState: () -> RecognizeNoteState,
    navigator: DestinationsNavigator?
) {
    Box {
        Scaffold() { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                when (generatedNoteState) {
                    is GeneratedNoteState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is GeneratedNoteState.Ready -> {
                        CenteredNavigationBarWithNavigateBack(
                            navigator = navigator,
                            label = stringResource(R.string.play_note_you_see)
                        )
                        if (viewModel != null) {
                            DisplaySheet(generatedNoteState.noteAndKeySignature, recognizeNoteState)
                        } else {
                            SheetInPreview()
                        }
                        SensitivitySlider(sensitivity = sensitivity) {
                            viewModel?.setSilenceTreashold(it)
                        }

                        Spacer(Modifier.height(50.dp))

                        FingerboardInputView(
                            scale = generatedNoteState.noteAndKeySignature.keySignature.getMajorScale()
                        ){
                            viewModel?.keyboardInoput(it)
                        }
                    }
                }
            }
        }
        HandleInputOverlay(
            recognizeNoteStateLambda = recognizeNoteState,
        )
    }
}

@Composable
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
                painter = painterResource(id = recognizeNoteState.iconAndColor.icon)
            )
        }
    }
}

@Composable
private fun AnimateFailure(visibility: Boolean) {
    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(5.dp, Color.Red, CircleShape)
        )
    }
}

@Composable
private fun InputStatusContent(
    modifier: Modifier,
    color: Color,
    painter: Painter,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(10.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painter, contentDescription = null, Modifier.size(150.dp), tint = color)
        }
    }
}

@Composable
private fun SensitivitySlider(sensitivity: () -> Float, setSensitivity: (Float) -> Unit) {
    Slider(
        value = sensitivity(),
        onValueChange = { setSensitivity((it).absoluteValue) },
    )
}

@Composable
private fun DisplaySheet(noteAndKeySignature: NoteAndKeySignature, recognizeNoteState: () -> RecognizeNoteState) {
    Sheet(
        notes = listOf(
            noteAndKeySignature.note
        ),
        extraNotes = (recognizeNoteState() as? RecognizeNoteState.Wrong)?.wrongNote?.let { listOf(it) } ?: listOf(),
        keySignature = noteAndKeySignature.keySignature
    )
}

@Preview
@Composable
fun RecognizeNotePreview() {
    RecognizeNoteScreenBody(
        viewModel = null,
        generatedNoteState = GeneratedNoteState.Ready(
            NoteAndKeySignature(
                note = SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    octave = 4,
                ),
                keySignature = KeySignature.Sharps(1)
            )
        ),
        sensitivity = { 0.5f },
        recognizeNoteState = { RecognizeNoteState.InTune() },
        navigator = null
    )
}