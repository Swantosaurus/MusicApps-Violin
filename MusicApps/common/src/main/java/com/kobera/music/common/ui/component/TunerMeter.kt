@file:Suppress("MagicNumber", "MaxLineLength", "MatchingDeclarationName")

package com.kobera.music.common.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kobera.music.common.R
import com.kobera.music.common.notes.InnerTwelveToneInterpretation
import com.kobera.music.common.notes.Tones
import com.kobera.music.common.notes.TwelveToneNames
import com.kobera.music.common.notes.frequency.ToneWithFrequency
import com.kobera.music.common.util.toStringWithNDecimals
import kotlin.math.min


/**
 *  Last note state for UI of tuner.
 */
sealed interface LastNoteState {
    class Silence(
        note: ToneWithFrequency = ToneWithFrequency(
            twelveNoteInterpretation = InnerTwelveToneInterpretation.A,
            name = TwelveToneNames.getName(InnerTwelveToneInterpretation.A),
            octave = 4,
            frequency = Tones.defaultA4Frequency,
        ),
        frequency: Double = Tones.defaultA4Frequency,
    ) : HasNote(
        note = note,
        frequency = frequency
    )

    open class HasNote(
        val note: ToneWithFrequency,
        val frequency: Double,
    ) : LastNoteState {

        fun getDifferenceAngle() = note.getDifferenceAngle(frequency, TUNER_METER_ANGLE / 2 / 8 * 7)
        fun isInTune() = note.isInTune(frequency)
    }
}

const val TUNER_METER_ANGLE = 180.0

@Composable
fun TunerMeter(modifier: Modifier = Modifier, noteStateLambda: () -> LastNoteState) {
    val noteState = noteStateLambda()
    BoxWithConstraints(modifier = modifier) {
        Column(modifier = Modifier.padding(20.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (noteState is LastNoteState.HasNote) noteState.note.name
                        + noteState.note.octave.toString() else "A4",
                Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = if (noteState is LastNoteState.HasNote)
                    noteState.frequency.toStringWithNDecimals(
                        2
                    )
                else "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
        AnimatedVisibility(
            visible = noteState is LastNoteState.Silence,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .alpha(0.75f)
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_music_off_24),
                    modifier = Modifier.size(80.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseOnSurface
                )
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
                    val numberOfRepeats = 7
                    repeat(numberOfRepeats) {
                        rotate(it / 14f * 360 - (90f - 90f / 7)) {
                            drawLine(
                                color = if (it == numberOfRepeats / 2) Color.Green else if (it == 0 || it == numberOfRepeats - 1) Color.Gray else Color.White,
                                start = start,
                                end = end,
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }


                    rotate(noteState.getDifferenceAngle().toFloat() - 180) {
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

/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation version 3 of the License,
 * or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */
