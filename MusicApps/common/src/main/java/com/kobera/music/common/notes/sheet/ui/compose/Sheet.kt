@file:Suppress("TooManyFunctions", "MagicNumber")
package com.kobera.music.common.notes.sheet.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.B
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.D
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.G
import com.kobera.music.common.notes.TwelvetoneTone
import com.kobera.music.common.notes.sheet.InnerSheetNote
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.SheetNote.SheetNoteParams.Accidental
import com.kobera.music.common.notes.sheet.ui.Clef
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.NotePath
import com.kobera.music.common.notes.sheet.ui.PathAndCenterOffset
import kotlin.math.ceil


@Suppress("LongParameterList")
@Composable
fun Sheet(
    modifier: Modifier = Modifier,
    notes: List<SheetNote>,
    extraNotes: List<SheetNote> = listOf(),
    keySignature: KeySignature,
    clef: Clef = Clef.Violin,
    height: Float = 240f,
    sheetViewModel: SheetViewModel = SheetViewModel()
) {
    val numebrOfLines = 5
    val lineHeight = height / numebrOfLines + 1
    val sheetSpacing = 100f
    val sheetSpacingFromLeft = 40f
    val clefPainter = painterResource(clef.resource)
    val density = LocalDensity.current
    val currentHeight by sheetViewModel.heightFlow.collectAsState()


    var leftOffsetForNotes : Float? = null

    Box(modifier.height(with(density){currentHeight.toDp()})) {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            var numberOfSheetIndex = 0
            var notesToDraw = notes
            var extraNotesToDraw = extraNotes
            while (notesToDraw.isNotEmpty() || extraNotesToDraw.isNotEmpty()) {
                var spcacingFromLeft = sheetSpacingFromLeft
                val topSpacing = numberOfSheetIndex * (height + sheetSpacing) + sheetSpacing
                translate(top = topSpacing) {
                    drawSheetLines(lineHeight = lineHeight)
                    spcacingFromLeft = drawClef(
                        notationHeight = lineHeight * 6,
                        spacingFromLeft = spcacingFromLeft,
                        painter = clefPainter
                    )
                    spcacingFromLeft = drawKeySignature(
                        spacingFromLeft = spcacingFromLeft,
                        keySignature = keySignature,
                        lineHeight = lineHeight
                    )
                    if(leftOffsetForNotes== null){
                        leftOffsetForNotes = spcacingFromLeft
                    }
                    notesToDraw = drawNotes(
                        keySignature = keySignature,
                        notes = notesToDraw,
                        lineHeight = lineHeight,
                        spacingFromLeft = spcacingFromLeft,
                        color = Black
                    )
                    extraNotesToDraw = drawNotes(
                        keySignature = keySignature,
                        notes = extraNotesToDraw,
                        lineHeight = lineHeight,
                        spacingFromLeft = leftOffsetForNotes!!,
                        color = Red
                    )
                }
                numberOfSheetIndex++
            }
            sheetViewModel.setHeight(numberOfSheetIndex * (height + sheetSpacing) + sheetSpacing)
        }
    }
}

@Composable
@Preview
@Suppress("LongMethod")
fun SheetPreview2() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Sheet(
            notes = listOf(
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.E,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.F,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.G,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.A,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.B,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.B,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.A,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.G,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.F,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.E,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
            ),
            keySignature = KeySignature.Sharps(7)
        )
    }
}


@Suppress("ReturnCount")
private fun DrawScope.drawKeySignature(
    spacingFromLeft: Float,
    keySignature: KeySignature,
    lineHeight: Float
): Float {
    return when (keySignature) {
        is KeySignature.Flats -> {
            if (keySignature.numberOfFlats == 0) {
                return spacingFromLeft
            }
            drawKeyFlats(
                numberOfFlats = keySignature.numberOfFlats,
                spacingFromLeft = spacingFromLeft,
                lineHeight = lineHeight
            )
        }

        is KeySignature.Sharps -> {
            if (keySignature.numberOfSharps == 0) {
                return spacingFromLeft
            }
            drawKeySharps(
                numberOfSharps = keySignature.numberOfSharps,
                spacingFromLeft = spacingFromLeft,
                lineHeight = lineHeight
            )
        }

        else -> {
            error("Unsupported key signature")
        }
    }
}

@Composable
@Preview
@Suppress("LongMethod")
fun SheetPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        Sheet(
            notes = listOf(
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.E,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.F,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.G,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.A,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.B,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.C,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 5
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.B,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.A,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.G,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.F,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.E,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
                SheetNote(
                    innerSheetNote = InnerSheetNote.D,
                    noteParams = SheetNote.SheetNoteParams(),
                    octave = 4
                ),
            ),
            keySignature = KeySignature.Flats(7)
        )
    }
}

@Composable
@Preview
fun SheetPreview3() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)) {
        Sheet(
            notes = listOf(
                SheetNote(InnerSheetNote.D, SheetNote.SheetNoteParams(accidental = Accidental.Flat), 4),
                SheetNote(InnerSheetNote.D, SheetNote.SheetNoteParams(accidental = Accidental.Sharp), 4),
            ),
            keySignature = KeySignature.Sharps(0))
    }
}

private fun DrawScope.drawKeyFlats(
    numberOfFlats: Int,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    val noteSpacing = lineHeight / 2
    var leftSpacing = spacingFromLeft
    repeat(numberOfFlats) { index ->
        var topOffset = 3 * noteSpacing + (index / 2) * noteSpacing
        if (index % 2 == 0) {
            topOffset += 3 * noteSpacing
        }

        translate(left = leftSpacing, top = topOffset) {
            drawFlat(sheetLineHeight = lineHeight, colorFilter = ColorFilter.tint(Black))
        }

        leftSpacing += lineHeight
    }
    return leftSpacing
}

private fun DrawScope.drawFlat(sheetLineHeight: Float, colorFilter: ColorFilter) {
    val height = 2 * sheetLineHeight
    val lineWidth = 2.dp.toPx()

    translate(top = -height * 3 / 4) {
        drawLine(
            Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = lineWidth,
            colorFilter = colorFilter
        )

        drawArc(
            color = Color.Black,
            startAngle = -135f,
            sweepAngle = 180f,
            useCenter = false,
            size = Size(sheetLineHeight / 2 + lineWidth, sheetLineHeight / 2),
            topLeft = Offset(-lineWidth, height / 2),
            style = Stroke(width = 2.dp.toPx()),
            colorFilter = colorFilter
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, height),
            end = Offset(sheetLineHeight / 2, height / 2 + (sheetLineHeight / 4 + lineWidth / 2)),
            strokeWidth = 2.dp.toPx(),
            colorFilter = colorFilter
        )
    }
}

private fun DrawScope.drawKeySharps(
    numberOfSharps: Int,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    val noteSpacing = lineHeight / 2
    var spacingLeft = spacingFromLeft
    repeat(numberOfSharps) { index ->
        var translateTop: Float = -(index / 2) * noteSpacing + 2 * noteSpacing
        if (index % 2 == 1) {
            translateTop += noteSpacing * 3f
        } else {
            if (index >= 4) {
                translateTop += noteSpacing * 7f
            }
        }


        translate(left = spacingLeft, top = translateTop) {
            drawSharp(lineHeight = lineHeight, colorFilter = ColorFilter.tint(Black))
        }
        spacingLeft += lineHeight
    }

    return spacingLeft + lineHeight
}

private fun DrawScope.drawSharp(lineHeight: Float, colorFilter: ColorFilter) {
    //vertical lines
    val height = 3f / 2 * lineHeight
    val width = height
    translate(top = -height / 2) {
        repeat(2) {
            drawLine(
                color = Color.Black,
                start = Offset(lineHeight / 2 + it * lineHeight / 2, 0f),
                end = Offset(lineHeight / 2 + it * lineHeight / 2, height),
                strokeWidth = 2.dp.toPx(),
                colorFilter = colorFilter
            )
        }

        //horizontal lines
        val yModulation = 5f
        repeat(2) {
            drawLine(
                color = Color.Black,
                start = Offset(0f, lineHeight / 2 + it * lineHeight / 2 + yModulation),
                end = Offset(width, lineHeight / 2 + it * lineHeight / 2 - yModulation),
                strokeWidth = 4.dp.toPx(),
                colorFilter = colorFilter
            )
        }
    }
}

@Suppress("unused")
private fun DrawScope.drawTimeSignature(spacingFromLeft: Float): Float {
    TODO()
}

private fun DrawScope.drawClef(
    notationHeight: Float,
    spacingFromLeft: Float,
    painter: Painter
): Float {
    val width = painter.intrinsicSize.width / painter.intrinsicSize.height * notationHeight
    translate(left = spacingFromLeft) {
        with(painter) {
            draw(
                Size(
                    width = width,
                    height = notationHeight
                )
            )
        }
    }

    return width + 50f
}

private fun DrawScope.drawSheetLines(lineHeight: Float) {
    repeat(5) {
        drawLine(
            color = Color.Black,
            start = Offset(0f, (it + 1) * lineHeight),
            end = Offset(size.width, (it + 1) * lineHeight)
        )
    }
}

/**
 *  draws notes until they fit in the screen and resutns list of notes that has not been drawn
 */
private fun DrawScope.drawNotes(
    keySignature: KeySignature,
    notes: List<SheetNote>,
    lineHeight: Float,
    spacingFromLeft: Float,
    color: Color
): List<SheetNote> {
    var leftSpacing = spacingFromLeft
    val notDrawnNotes = mutableListOf<SheetNote>()

    for (note in notes) {
        if (leftSpacing >= size.width - 150f) {
            notDrawnNotes += note
            continue
        }

        leftSpacing += drawAccidental(
            note = note,
            keySignature = keySignature,
            spacingFromLeft = leftSpacing,
            lineHeight = lineHeight,
            colorFilter = ColorFilter.tint(color = color)
        )


        leftSpacing += drawNote(
            note = note,
            lineHeight = lineHeight,
            spacingFromLeft = leftSpacing,
            color = color
        )
    }
    return notDrawnNotes
}

private fun DrawScope.drawAccidental(
    note: SheetNote,
    keySignature: KeySignature,
    spacingFromLeft: Float,
    lineHeight: Float,
    colorFilter: ColorFilter
): Float {
    val accidentalToDraw: Accidental? = note.getAccidentalToDraw(keySignature = keySignature)
    val noteSpacing = lineHeight / 2
    if(accidentalToDraw == null) {
        return 0f
    }
    translate(
        left = spacingFromLeft,
        top = -noteSpacing * (note.sheetDifference(TwelvetoneTone(G, 5)) - 1)
    ) {
        when (accidentalToDraw) {
            Accidental.None -> drawNatural(lineHeight = lineHeight, colorFilter = colorFilter)
            Accidental.Sharp -> drawSharp(lineHeight = lineHeight, colorFilter = colorFilter)
            Accidental.Flat -> drawFlat(sheetLineHeight = lineHeight, colorFilter = colorFilter)
            else -> TODO()
        }
    }
    return 90f
}


@Suppress("CyclomaticComplexMethod")
private fun SheetNote.getAccidentalToDraw(keySignature: KeySignature): Accidental? {
    if (!isInKeySignature(keySignature)) {
        return when (noteParams.accidental) {
            Accidental.None -> {
                /* nothing to add 👍 */
                null
            }

            Accidental.Sharp -> {
                Accidental.Sharp
            }

            Accidental.Flat -> {
                Accidental.Flat
            }

            else -> TODO()
        }
    }
    return when (keySignature) {
        is KeySignature.Flats -> {
            when (noteParams.accidental) {
                Accidental.Flat -> {
                    /* nothing to add 👍 */
                    null
                }

                Accidental.None -> {
                    Accidental.None
                }

                Accidental.Sharp -> {
                    Accidental.Sharp
                }

                else -> TODO()
            }
        }

        is KeySignature.Sharps -> {
            when (noteParams.accidental) {
                Accidental.Flat -> {
                    Accidental.Flat
                }

                Accidental.None -> {
                    Accidental.None
                }

                Accidental.Sharp -> {
                    /* nothing to add 👍 */
                    null
                }

                else -> TODO()
            }
        }

        else -> error("Unsupported key signature")
    }
}

private fun DrawScope.drawNatural(lineHeight: Float, colorFilter: ColorFilter): Float {
    val width = lineHeight
    val height = lineHeight * 2
    translate(top = -height / 2) {
        drawLine(
            Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, height * 3 / 4 + 2.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            colorFilter = colorFilter
        )
        drawLine(
            Color.Black,
            start = Offset(width, height / 4 - 2.dp.toPx()),
            end = Offset(width, height),
            strokeWidth = 2.dp.toPx(),
            colorFilter = colorFilter
        )
        drawLine(
            Color.Black,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 4),
            strokeWidth = 4.dp.toPx(),
            colorFilter = colorFilter
        )
        drawLine(
            Color.Black,
            start = Offset(0f, height * 3 / 4),
            end = Offset(width, height / 2),
            strokeWidth = 4.dp.toPx(),
            colorFilter = colorFilter
        )
    }
    return width
}

private fun SheetNote.isInKeySignature(keySignature: KeySignature): Boolean =
    keySignature.getKeySignatureNotes().contains(this.innerSheetNote)


private fun DrawScope.drawNote(
    note: SheetNote,
    lineHeight: Float,
    spacingFromLeft: Float,
    color: Color
): Float {
    val noteWidth = 100f
    val noteStep = lineHeight / 2
    var notePath: PathAndCenterOffset =
        getNotePathAndCenterOffset(note = note, lineHeight = lineHeight)


    translate(top = noteStep * (-note.sheetDifference(TwelvetoneTone(G, 5)))) {
        translate(left = spacingFromLeft, top = -notePath.centerOffset.y + noteStep) {
            drawPath(notePath.path, color = color)
        }
    }


    val supportingLinesAtTop
        = ceil(note.sheetDifference(TwelvetoneTone(G, 5)).toDouble() / 2).toInt()
    repeat(supportingLinesAtTop){
        translate (
            left = spacingFromLeft-25f,
            top = it * (- 2 * noteStep)
        ){
            drawSupportingLine(color = color)
        }
    }
    val supportingLinesAtBottom
        = ceil( - note.sheetDifference(TwelvetoneTone(D, 4)).toDouble() / 2).toInt()
    repeat(supportingLinesAtBottom){
        translate (
            left = spacingFromLeft-25f,
            top = it * (2 * noteStep) + noteStep * 12
        ){
            drawSupportingLine(color = color)
        }
    }

    return noteWidth
}


private fun DrawScope.drawSupportingLine(color: Color){
    drawLine(
        color = color,
        start = Offset.Zero,
        end = Offset(115f, 0f),
        strokeWidth = 1.dp.toPx()
    )
}


private fun getNotePathAndCenterOffset(note: SheetNote, lineHeight: Float): PathAndCenterOffset =
    when (note.noteParams.duration) {
        SheetNote.SheetNoteParams.Duration.Quarter -> {
            if (note > TwelvetoneTone(G, octave = 5) || note < TwelvetoneTone(D, 3)) {
                //TODO()
                PathAndCenterOffset(Path(), Offset.Zero)
            }

            NotePath.drawQuarterNote(
                lineHeightPx = lineHeight,
                legLength = lineHeight * 3,
                facingDown = note > TwelvetoneTone(B, 4)
            )
        }

        else -> {
            TODO()
        }
    }


/**
 * This file is part of MusicApps.
 *
 * MusicApps is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation version 3 of the License, or any later version.
 *
 * MusicApps is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  Full licence is in LICENSE file in root directory or at https://www.gnu.org/licenses/gpl-3.0.txt
 */