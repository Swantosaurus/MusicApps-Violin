@file:Suppress("MagicNumber")

package com.kobera.music.common.notes.sheet.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import timber.log.Timber

/**
 * Path for drawing a note
 * returns path with offset of center - user can move it to the right place without handeling its orientation
 */
object NotePath {
    const val widthToHeightRatio = 1.3f

    fun drawLeglesNote(heightPx: Float): PathAndCenterOffset {
        val body = getFilledBodyPath(lineHeightPx = heightPx, offset = Offset.Zero)
        return PathAndCenterOffset(path = body.path, body.center)
    }


    fun drawLegedNote(
        heightPx: Float,
        legLength: Float,
        facingDown: Boolean,
        flags: Int = 0,
        filledLeg: Boolean
    ): PathAndCenterOffset {
        val body = if (facingDown) {
            getFilledBodyPath(lineHeightPx = heightPx, offset = Offset.Zero)
        } else {
            getFilledBodyPath(lineHeightPx = heightPx, Offset(0f, legLength))
        }

        val noteBodyCenterY = if (facingDown) {
            body.center.y
        } else {
            body.center.y + legLength
        }

        val path = body.path.addLeg(
            facingDown = facingDown,
            notePathWitCenterOffset = body,
            legLength = legLength,
            noteBodyCenterY = noteBodyCenterY,
            filledLeg = filledLeg
        ).addFlags(
            facingDown = facingDown,
            legLength = legLength,
            noteBody = body,
            numberOfFlags = flags,
        )

        return PathAndCenterOffset(
            path = path,
            centerOffset = Offset(body.center.x, noteBodyCenterY)
        )
    }

    private fun Path.addFlags(
        facingDown: Boolean,
        legLength: Float,
        noteBody: ElementPathInformation,
        numberOfFlags: Int,
    ): Path {
        return this.apply {
            repeat(numberOfFlags) { index ->
                val move = index * 40f
                val positionOffsetY =  if(facingDown) {
                    legLength - move + noteBody.center.y
                } else {
                    move
                }


                val offsetX = if(facingDown) noteBody.start.x + legWidth else noteBody.end.x

                moveTo(
                    offsetX,
                    positionOffsetY.plus(if(facingDown) -30f else 30f)
                )
                Timber.d("${30f.apply { if(facingDown) unaryMinus() }}")

                val yMove1 = if(facingDown) -20f else 20f
                val yMove2 = if(facingDown) -50f else 50f
                val yMove3 = if(facingDown) -70f else 70f

                

                val p1  = Offset(0f  + offsetX, positionOffsetY + yMove1)
                val p2  = Offset(35f + offsetX, positionOffsetY + yMove2)
                val end = Offset(30f + offsetX, positionOffsetY + yMove3)

                cubicTo(
                    p1.x, p1.y,
                    p2.x, p2.y,
                    end.x, end.y
                )

                cubicTo(
                    p2.x + 10f, if(!facingDown) p2.y - 10f else p2.y + 10f,
                    p1.x, if(!facingDown) p1.y - 15f else p1.y + 15f,
                    offsetX, positionOffsetY
                )
            }

        }
    }

    private fun Path.addLeg(
        facingDown: Boolean,
        notePathWitCenterOffset: ElementPathInformation,
        legLength: Float,
        noteBodyCenterY: Float,
        filledLeg: Boolean
    ) =
        apply {
            if (facingDown) {
                if(!filledLeg) {
                    moveTo(x = 0f, y = noteBodyCenterY)
                    lineTo(x = legWidth, noteBodyCenterY + legLength)
                } else {
                    addRect(
                        Rect(
                            Offset(x = 0f, y = noteBodyCenterY),
                            Offset(x = legWidth, noteBodyCenterY + legLength)
                        )
                    )
                }
            } else {
                if(!filledLeg) {
                    moveTo(x = notePathWitCenterOffset.end.x - legWidth, y = 0f)
                    lineTo(x = notePathWitCenterOffset.end.x, y = noteBodyCenterY)
                } else {
                    addRect(
                        Rect(
                            Offset(x = notePathWitCenterOffset.end.x - legWidth, y = 0f),
                            Offset(x = notePathWitCenterOffset.end.x, y = noteBodyCenterY)
                        )
                    )
                }
            }
        }



    private const val legWidth = 5f


    private data class ElementPathInformation(
        val path: Path,
        val start: Offset,
        val end: Offset,
        val center: Offset,
    )

    private fun getFilledBodyPath(lineHeightPx: Float, offset: Offset): ElementPathInformation {
        val endOffset = offset + Offset(lineHeightPx * 1.3f, lineHeightPx)
        val path = Path().apply {
            addOval(oval = Rect(offset, endOffset))

            close()
        }

        return ElementPathInformation(
            path = path,
            start = Offset.Zero,
            end = endOffset,
            center = (endOffset - offset) / 2f
        )
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


