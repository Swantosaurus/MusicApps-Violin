@file:Suppress("MagicNumber")

package com.kobera.music.common.notes.sheet.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path

/**
 * Path for drawing a note
 */
object NotePath {
    fun drawQuarterNote(lineHeightPx: Float, legLength: Float, facingDown: Boolean): PathAndCenterOffset {
        val body = if (facingDown) {
            getFilledNoteBodyPath(lineHeightPx = lineHeightPx, offset = Offset.Zero)
        } else {
            getFilledNoteBodyPath(lineHeightPx = lineHeightPx, Offset(0f, legLength))
        }

        val noteBodyCenterY = if(facingDown) {
            body.centerOffset.y
        } else {
            body.centerOffset.y + legLength
        }
        val path = body.path.apply {
            if (facingDown) {
                addRect(
                    Rect(
                        Offset(x = 0f, y = noteBodyCenterY),
                        Offset(x = legWidth, noteBodyCenterY + legLength)
                    )
                )
            } else {
                addRect(
                    Rect(
                        Offset(x = body.end.x - legWidth, y = 0f),
                        Offset(x = body.end.x, y = noteBodyCenterY)
                    )
                )
            }
        }
        return PathAndCenterOffset(path = path, Offset(body.centerOffset.x, noteBodyCenterY))
    }

    private const val legWidth = 5f


    private data class ElementPathInformation(
        val path: Path,
        val start: Offset,
        val end: Offset,
        val centerOffset: Offset,
    )

    private fun getFilledNoteBodyPath(lineHeightPx: Float, offset: Offset): ElementPathInformation {
        val endOffset = offset + Offset(lineHeightPx * 1.3f, lineHeightPx)
        val path = Path().apply {
            addOval(oval = Rect(offset, endOffset))

            close()
        }

        return ElementPathInformation(
            path = path,
            start = Offset.Zero,
            end = endOffset,
            centerOffset = (endOffset - offset) / 2f
        )
    }
}


