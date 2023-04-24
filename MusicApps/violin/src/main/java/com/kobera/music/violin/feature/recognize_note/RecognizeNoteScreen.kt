package com.kobera.music.violin.feature.recognize_note

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.*
import com.kobera.music.common.notes.sheet.InnerSheetNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.compose.Sheet
import com.kobera.music.common.ui.component.HandleAudioPermission
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber
import kotlin.math.absoluteValue

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun RecognizeNoteScreen(viewModel: RecognizeNoteViewModel = getViewModel()) {
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
                recognizeNoteState = { recognizeNoteState }
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
    recognizeNoteState: () -> RecognizeNoteState
) {
    Scaffold() { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (generatedNoteState) {
                is GeneratedNoteState.Loading -> {
                    CircularProgressIndicator()
                }

                is GeneratedNoteState.Ready -> {
                    if(viewModel != null) {
                        DisplaySheet(generatedNoteState.noteAndKeySignature)
                    } else {
                        Canvas(modifier = Modifier.height(200.dp)){
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
                    SensitivitySlider(sensitivity = sensitivity) {
                        viewModel?.setSilenceTreashold(it)
                    }
                    HandleRecognizeNoteState(
                        recognizeNoteStateLambda = recognizeNoteState,
                        generateNewNote = {
                            viewModel?.generateRandomNote()
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun HandleRecognizeNoteState(
    recognizeNoteStateLambda: () -> RecognizeNoteState,
    generateNewNote: () -> Unit,
) {
    val recognizeNoteState = recognizeNoteStateLambda()
    val coroutineScope = rememberCoroutineScope()

    var lockedUi by remember { mutableStateOf(false) }
    var successVisibility: Boolean by remember { mutableStateOf(false) }
    var failureVisibility: Boolean by remember { mutableStateOf(false) }
    var successColor: Color by remember {
        mutableStateOf(Color.Green)
    }
    AnimateSuccess(visibility = successVisibility, color = successColor)
    AnimateFailure(visibility = failureVisibility)

    if (lockedUi) return
    when (recognizeNoteState) {
        is RecognizeNoteState.InTune -> {
            lockedUi = true
            successColor = Color.Green
            successVisibility = true
            coroutineScope.launch {
                delay(1000)
                successVisibility = false
                delay(200)
                generateNewNote()
                lockedUi = false
            }
        }

        is RecognizeNoteState.CorrectNotInTune -> {
            lockedUi = true
            successColor = Color.LightGray
            successVisibility = true
            coroutineScope.launch {
                delay(1000)
                successVisibility = false
                delay(200)
                generateNewNote()
                lockedUi = false
            }
        }

        is RecognizeNoteState.Wrong -> {
            lockedUi = true
            failureVisibility = true
            coroutineScope.launch {
                delay(1000)
                failureVisibility = false
                delay(200)
                generateNewNote()
                lockedUi = false
            }
        }

        is RecognizeNoteState.Silence -> {

            // do nothing
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
private fun AnimateSuccess(visibility: Boolean, color: Color) {
    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn() + fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(5.dp, color, CircleShape)
        )
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
private fun DisplaySheet(noteAndKeySignature: NoteAndKeySignature) {
    var height by remember {
        mutableStateOf(240.dp)
    }
    Timber.d("height: $height")
    Sheet(
        notes = listOf(
            noteAndKeySignature.note
        ),
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
        recognizeNoteState = { RecognizeNoteState.InTune }
    )
}