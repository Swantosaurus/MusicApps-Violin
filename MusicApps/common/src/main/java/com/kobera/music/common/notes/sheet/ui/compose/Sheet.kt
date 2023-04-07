package com.kobera.music.common.notes.sheet.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kobera.music.common.notes.BasicNote
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.B
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.D
import com.kobera.music.common.notes.InnerTwelveToneInterpretation.G
import com.kobera.music.common.notes.sheet.SheetNote
import com.kobera.music.common.notes.sheet.ui.Clef
import com.kobera.music.common.notes.sheet.ui.KeySignature
import com.kobera.music.common.notes.sheet.ui.NotePath


@Composable
fun Sheet(
    modifier: Modifier = Modifier,
    notes: List<SheetNote>,
    clef: Clef = Clef.Violin,
    keySignature: KeySignature
) {
    val height = 240f
    val lineHeight = height / 6
    val sheetSpacing = 100f
    val sheetSpacingFromLeft = 20f
    //Icon(painter = painterResource(id = R.drawable.test), contentDescription = null)
    val clefPainter = painterResource(clef.resource)
    Canvas(
        modifier = Modifier
            //.height(300.dp)
            .fillMaxWidth()
    ) {
        var notesToDraw = notes
        var index = 0
        while (notesToDraw.isNotEmpty()) {
            var spcacingFromLeft = sheetSpacingFromLeft
            translate(top = index * (height + sheetSpacing) + sheetSpacing) {
                drawSheetLines(lineHeight = lineHeight)


                spcacingFromLeft = drawClef(
                    notationHeight = height,
                    spacingFromLeft = spcacingFromLeft,
                    painter = clefPainter
                )
                //spcacingFromLeft = drawTimeSignature(spacingFromLeft = spcacingFromLeft)
                spcacingFromLeft = drawKeySignature(
                    spacingFromLeft = spcacingFromLeft,
                    keySignature = keySignature,
                    lineHeight = lineHeight
                )
                notesToDraw = drawNotes(
                    notes = notesToDraw,
                    lineHeight = lineHeight,
                    spacingFromLeft = spcacingFromLeft
                )
            }
            index++
        }
    }
}

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
            throw IllegalStateException()
        }
    }
}

private fun DrawScope.drawKeyFlats(
    numberOfFlats: Int,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    TODO()
}

private fun DrawScope.drawKeySharps(
    numberOfSharps: Int,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    var spacingLeft = spacingFromLeft
    repeat(numberOfSharps) { index ->
        var translateTop: Float = - (index / 2) * lineHeight * 1 / 2
        if (index % 2 == 1) {
            translateTop += lineHeight * 3f / 2
        } else {
            if (index >= 4) {
                translateTop += lineHeight * 7f / 2
            }
        }


        translate(left = spacingLeft, top = translateTop) {
            drawSharp(lineHeight = lineHeight)
        }
        spacingLeft += lineHeight * 1.5f
    }

    return spacingLeft + lineHeight
}

private fun DrawScope.drawSharp(lineHeight: Float) {
    //vertical lines
    val height = 3f / 2 * lineHeight
    val width = height
    translate(top = height / 2 - lineHeight / 2) {
        repeat(2) {
            drawLine(
                color = Color.Black,
                start = Offset(lineHeight / 2 + it * lineHeight / 2, 0f),
                end = Offset(lineHeight / 2 + it * lineHeight / 2, height),
                strokeWidth = 2.dp.toPx()
            )
        }

        //horizontal lines
        val yModulation = 5f
        repeat(2) {
            drawLine(
                color = Color.Black,
                start = Offset(0f, lineHeight / 2 + it * lineHeight / 2 + yModulation),
                end = Offset(width, lineHeight / 2 + it * lineHeight / 2 - yModulation),
                strokeWidth = 6.dp.toPx()
            )
        }
    }
}

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

    return width + 30f
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
    notes: List<SheetNote>,
    lineHeight: Float,
    spacingFromLeft: Float
): List<SheetNote> {
    val noteStep = lineHeight / 2
    var leftSpacing = spacingFromLeft
    val notDrawnNotes = mutableListOf<SheetNote>()
    val noteWidth = 100f

    for (note in notes) {
        if (leftSpacing >= size.width - noteWidth) {
            notDrawnNotes += note
            continue
        }
        when (note.noteParams.duration) {
            SheetNote.SheetNoteParams.Duration.Quarter -> {
                if (note > BasicNote(G, octave = 5) || note < BasicNote(D, 3)) {
                    TODO()
                }
                val notePath = NotePath.drawQuarterNote(
                    lineHeightPx = lineHeight,
                    legLength = 150f,
                    facingDown = note > BasicNote(B, 4)
                )
                translate(top = noteStep * (-note.sheetDifference(BasicNote(G, 5)))) {
                    translate(left = leftSpacing, top = -notePath.centerOffset.y + noteStep) {
                        drawPath(notePath.path, Color.Black)
                    }
                }

                leftSpacing += noteWidth
            }

            else -> {
                TODO()
            }
        }
    }

    return notDrawnNotes
}