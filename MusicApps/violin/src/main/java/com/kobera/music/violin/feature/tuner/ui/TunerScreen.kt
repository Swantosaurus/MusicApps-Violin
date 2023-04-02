package com.kobera.music.violin.feature.tuner.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.kobera.music.common.util.toStringWithNDecimals
import com.kobera.music.violin.R
import com.kobera.music.violin.sound.notes.violinStrings
import com.kobera.music.violin.utils.setSystemBarColors
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel
import java.lang.Float.min
import kotlin.math.absoluteValue

@Destination
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TunerScreen(tunerViewModel: TunerViewModel = getViewModel()) {
    val permission = rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
    val noteState by tunerViewModel.note.collectAsStateWithLifecycle()
    val sensitivity by tunerViewModel.sensitivity.collectAsStateWithLifecycle(initialValue = 0.2f)
    val a4frequency by tunerViewModel.a4Frequency.frequency.collectAsStateWithLifecycle()
    val notesInTunerState by tunerViewModel.notesInTuner.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        permission.launchPermissionRequest()
    }



    when (permission.status) {
        is PermissionStatus.Granted -> {
            DisposableEffect(key1 = Unit) {
                tunerViewModel.startRecording()
                onDispose { tunerViewModel.stopRecording() }
            }

            TunerScreenBody(
                tunerViewModel = tunerViewModel,
                noteState = noteState,
                sensitivity = { sensitivity },
                a4frequency = { a4frequency },
                { notesInTunerState })
        }

        is PermissionStatus.Denied -> {
            if ((permission.status as PermissionStatus.Denied).shouldShowRationale) {
                ShowRationale(permissionState = permission)
            } else {
                OpenSettingsOrRestartApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TunerScreenBody(
    tunerViewModel: TunerViewModel?,
    noteState: LastNoteState,
    sensitivity: () -> Float,
    a4frequency: () -> Double,
    notesInTunerState: () -> NotesInTunerState,
) {
    setSystemBarColors(darkIconsTopBar = false)
    Scaffold(
        floatingActionButton = {
            Surface(
                shape = CircleShape,
                tonalElevation = 4.dp
            ){
                OnlyViolinNotes(notesInTunerState = notesInTunerState(), setTunerNotes = { to -> tunerViewModel?.setTunerNotes(to) })
            }
        }
    ) { paddingValues ->
        val direction = LocalLayoutDirection provides LayoutDirection.Rtl
        Column(
            Modifier
                .padding(
                    start = paddingValues.calculateLeftPadding(layoutDirection = direction.value),
                    end = paddingValues.calculateRightPadding(direction.value),
                    bottom = paddingValues.calculateBottomPadding()
                )
                .fillMaxSize(),
            horizontalAlignment = CenterHorizontally
        ) {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.headlineMedium
                        .copy(color = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    FrequencySetting(
                        modifier = Modifier.fillMaxWidth(),
                        a4frequency = a4frequency().toInt(),
                        setFrequency = { tunerViewModel?.setA4Frequency(it.toDouble()) }
                    )
                    Divider()

                    TunerMeter(
                        Modifier.aspectRatio(11f / 9),
                        noteState = noteState
                    )
                }
            }


            SensitivitySetting(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(top = 20.dp),
                setSensitivity = { tunerViewModel?.setSensitivity(it) },
                sensitivity = sensitivity
            )
            Box(Modifier.fillMaxSize(), contentAlignment = Center) {
                ViolinStrings(noteState = noteState)
            }
        }
    }
}

@Composable
fun ViolinStrings(noteState: LastNoteState) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val errorColor = MaterialTheme.colorScheme.error
    BoxWithConstraints(
        Modifier
            .height(250.dp)
            .fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stringGradient = Brush.linearGradient(
                listOf(Color.Gray, Color.DarkGray),
                tileMode = TileMode.Mirror,
                start = Offset(0f, 0f),
                end = Offset(15f, 15f),
            )

            val selectedNoteNotInTune = Brush.linearGradient(
                listOf(errorColor, Color.DarkGray),
                tileMode = TileMode.Mirror,
                start = Offset(0f, 0f),
                end = Offset(15f, 15f),
            )

            val selectedNoteInTune =  Brush.linearGradient(
                listOf(primaryColor, Color.DarkGray),
                tileMode = TileMode.Mirror,
                start = Offset(0f, 0f),
                end = Offset(15f, 15f),
            )

            val leftSpacing = 60f
            val rightSpacing = 80f
            val stringGrowing = 1.2f
            val violinStringsFromE = violinStrings.reversedArray()

            repeat(times = 4){index ->
                drawLine(
                    brush =
                    if(noteState !is LastNoteState.Silence
                        && noteState is LastNoteState.HasNote
                        && violinStringsFromE.indexOfFirst { noteState.note.name == it } == index) {
                        if(noteState.isInTune()) {
                            selectedNoteInTune
                        } else {
                            selectedNoteNotInTune
                        }
                    } else {
                       stringGradient
                    }
                               ,
                    start = Offset(0f, leftSpacing/2 + index * leftSpacing),
                    end = Offset(maxWidth.value.dp.toPx(), 0f + index * rightSpacing),
                    strokeWidth = (5 + index * stringGrowing).dp.toPx()
                )
            }
        }
    }

}

@Composable
private fun SensitivitySetting(modifier: Modifier = Modifier, setSensitivity: (Double) -> Unit, sensitivity: () -> Float){
    Column(modifier.padding(horizontal = 10.dp)) {
        Text(modifier = Modifier.fillMaxWidth(), text = "Sensitivity", textAlign = TextAlign.Center)
        Row(Modifier.verticalScroll(rememberScrollState())) {
            Text(text = "Low")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "High")
        }
        Slider(value = (sensitivity()-1).absoluteValue, onValueChange = {
            setSensitivity(((it.toDouble()-1).absoluteValue))
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrequencySetting(modifier : Modifier = Modifier, a4frequency: Int, setFrequency: (Int) -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { setFrequency(a4frequency - 1) }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_remove_24),
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                contentDescription = null
            )
        }

        Text(text = a4frequency.toString())

        IconButton(onClick = { setFrequency(a4frequency + 1) }) {
            Icon(
                imageVector = Icons.Default.Add,
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun OnlyViolinNotes(
    notesInTunerState: NotesInTunerState,
    setTunerNotes: (NotesInTunerState) -> Unit
) {
    FilledIconToggleButton(
        checked = notesInTunerState is NotesInTunerState.ViolinNotes,
        onCheckedChange = {
            setTunerNotes(
                if (it) {
                    NotesInTunerState.ViolinNotes()
                } else {
                    NotesInTunerState.AllNotes()
                }
            )
        },
        colors = IconButtonDefaults.iconToggleButtonColors(
            //containerColor = MaterialTheme.colorScheme.surfaceVariant,
            checkedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_violin),
            modifier = Modifier.offset(0.dp, (-3).dp),
            contentDescription = stringResource(
                R.string.toggle_only_violin_notes
            )
        )
    }
}

@Composable
private fun TunerMeter(modifier: Modifier = Modifier, noteState: LastNoteState) {
    BoxWithConstraints(modifier = modifier){
        Column (modifier = Modifier.padding(20.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (noteState is LastNoteState.HasNote) noteState.note.name else "A4",
                Modifier.align(CenterHorizontally)
            )
            Text(
                text = if (noteState is LastNoteState.HasNote) noteState.frequency.toStringWithNDecimals(
                    2
                )
                else "",
                Modifier
                    .align(CenterHorizontally)
                    .padding(bottom = 10.dp)
            )
            TunerClock(
                noteState = when (noteState) {
                    is LastNoteState.HasNote -> {
                        noteState
                    }
                }
            )
        }
        AnimatedVisibility(visible = noteState is LastNoteState.Silence, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .alpha(0.75f)
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.round_music_off_24), modifier = Modifier.size(80.dp), contentDescription = null, tint = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }
    }
}

@Composable
private fun TunerClock(modifier: Modifier = Modifier, noteState: LastNoteState.HasNote) {
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            val diameter = min(size.width, size.height) * 0.85f
            val radius = diameter / 2


            translate(top = size.height / 2 - 30) {
                scale(2f) {
                    val start = center - Offset(0f, radius)
                    val end = start + Offset(0f, radius / 20f)
                    repeat(7) {
                        rotate(it / 14f * 360 - (90f - 90f / 7)) {
                            drawLine(
                                color = Color.White,
                                start = start,
                                end = end,
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }


                    rotate(noteState.differenceAngle.toFloat() - 180) {
                        drawLine(
                            color = if (noteState.isInTune()) primary else error,
                            start = center,
                            end = center + Offset(0f, radius * 0.9f),
                            strokeWidth = 6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(
                        color = if (noteState.isInTune()) primary else error,
                        radius = 8.dp.toPx(),
                        center = center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ShowRationale(permissionState: PermissionState) {
    Column() {
        Text(text = "Microphone permission is required to use the tuner")
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text(text = "Allow")
        }
    }
}

@Composable
private fun OpenSettingsOrRestartApp() {
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri

    Column() {
        Text(text = "Microphone permission is required to use the tuner")
        Button(onClick = { context.startActivity(intent) }) {
            Text(text = "Open settings")
        }
    }
}

