@file:Suppress("LongMethod", "MagicNumber")

package com.kobera.music.violin.feature.fingerboardInput

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.kobera.music.common.notes.TwelvetoneTone
import com.kobera.music.common.notes.scale.Scale
import timber.log.Timber


/**
 * FingerboardInput
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun FingerboardInputView(scale: Scale, keyboardClicked: (TwelvetoneTone) -> Unit) {
    val fingerPositions = remember{
        FingerboardMapping.toFingerboard(scale = scale).stringsFirgerboardPosition.reversed()
    }

    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints() {
        val leftSpacing = remember {
            60.dp
        }
        val rightSpacing = remember {
            76.dp
        }

        val ld = LocalDensity.current


        Canvas(
            modifier = Modifier
                .height(rightSpacing* 4 + 50.dp)
                .fillMaxWidth()
        ) {
            repeat(times = 4) { drawStringIndex ->
                val singleStringFingerboard =
                    fingerPositions[drawStringIndex]

                val stringGrowing = 2f

                val start = Offset(0f, leftSpacing.toPx() / 2 + drawStringIndex * leftSpacing.toPx())
                val end = Offset(size.width, 0f + drawStringIndex * rightSpacing.toPx())


                drawLine(
                    brush = Brush.linearGradient(
                        listOf(Color.Gray, Color.DarkGray),
                        tileMode = TileMode.Mirror,
                        start = Offset(0f, 0f),
                        end = Offset(15f, 15f),
                    ),
                    start = start,
                    end = end,
                    strokeWidth = (5 + drawStringIndex * stringGrowing).dp.toPx()
                )
            }
        }


        with(ld) {
            fingerPositions.forEachIndexed { drawStringIndex, singleStringFingerboard ->
                val start = Offset(0f, leftSpacing.toPx() / 2 + drawStringIndex * leftSpacing.toPx())
                val end = Offset(maxWidth.toPx() , 0f + drawStringIndex * rightSpacing.toPx())
                singleStringFingerboard.fingerPositions.forEach { fingerPosition ->
                    val xbuttonOffset = maxWidth / 9 * fingerPosition.positionOffset
                    Button(modifier = Modifier
                        .size(50.dp)
                        .offset(
                        x = xbuttonOffset,
                        y = calculateLineEquationForX(
                            start,
                            end,
                            xbuttonOffset.toPx()
                        ).toDp() - 25.dp
                    ), onClick = { keyboardClicked(fingerPosition.respondingNote) }, shape = CircleShape, contentPadding = PaddingValues()) {
                        Text(fingerPosition.fingerNumber.toString())
                    }
                }
            }
        }
    }
}

private fun calculateLineEquationForX(point1: Offset, point2: Offset, x: Float): Float {
    val a = (point2.y - point1.y) / (point2.x - point1.x)

    val result = a * x + point1.y
    Timber.d("result = $result /// ${point1.y}")
    return result
}