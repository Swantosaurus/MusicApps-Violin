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
import com.kobera.music.common.notes.sheet.SheetNote.SheetNoteParams.Accidental
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
                    keySignature = keySignature,
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

private fun DrawScope.drawFlat(lineHeight: Float) {
    TODO()
}

private fun DrawScope.drawKeySharps(
    numberOfSharps: Int,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    val noteSpacing = lineHeight / 2
    var spacingLeft = spacingFromLeft
    repeat(numberOfSharps) { index ->
        var translateTop: Float = -(index / 2) * noteSpacing
        if (index % 2 == 1) {
            translateTop += noteSpacing * 3f
        } else {
            if (index >= 4) {
                translateTop += noteSpacing * 7f
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
                strokeWidth = 4.dp.toPx()
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
    keySignature: KeySignature,
    notes: List<SheetNote>,
    lineHeight: Float,
    spacingFromLeft: Float
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
            lineHeight = lineHeight
        )


        leftSpacing += drawNote(
            note = note,
            lineHeight = lineHeight,
            spacingFromLeft = leftSpacing
        )
    }
    return notDrawnNotes
}

private fun DrawScope.drawAccidental(
    note: SheetNote,
    keySignature: KeySignature,
    spacingFromLeft: Float,
    lineHeight: Float
): Float {
    val accidentalToDraw: Accidental? = note.getAccidentalToDraw(keySignature = keySignature)
    val noteSpacing = lineHeight / 2
    accidentalToDraw?.let {
        translate(
            left = spacingFromLeft,
            top = -noteSpacing * (note.sheetDifference(BasicNote(G, 5)) - 1)
        ) {
            when (accidentalToDraw) {
                Accidental.None -> drawNatural(lineHeight = lineHeight)
                Accidental.Sharp -> drawSharp(lineHeight = lineHeight)
                Accidental.Flat -> drawFlat(lineHeight = lineHeight)
                else -> TODO()
            }
        }


        return 75f
    }
    return 0f
}


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

        else -> throw java.lang.IllegalStateException()
    }
}

private fun DrawScope.drawNatural(lineHeight: Float): Float {
    val width = lineHeight
    val height = lineHeight * 2
    translate(top = -height / 2) {
        drawLine(
            Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, height * 3 / 4),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            Color.Black,
            start = Offset(width, height / 4),
            end = Offset(width, height),
            strokeWidth = 2.dp.toPx()
        )
        drawLine(
            Color.Black,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 4),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            Color.Black,
            start = Offset(0f, height * 3 / 4),
            end = Offset(width, height / 2),
            strokeWidth = 4.dp.toPx()
        )
    }
    return width
}

private fun SheetNote.isInKeySignature(keySignature: KeySignature): Boolean =
    keySignature.getKeySignatureNotes().contains(this.innerSheetNote)


private fun DrawScope.drawNote(
    note: SheetNote,
    lineHeight: Float,
    spacingFromLeft: Float
): Float {
    val noteWidth = 100f
    val noteStep = lineHeight / 2
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
                translate(left = spacingFromLeft, top = -notePath.centerOffset.y + noteStep) {
                    drawPath(notePath.path, Color.Black)
                }
            }
        }

        else -> {
            TODO()
        }
    }

    return noteWidth
}