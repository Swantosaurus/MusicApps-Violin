package com.kobera.music.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.AccuracyGraph(success: Int, fails: Int) {
    AccuracyGraph(success = success.toDouble(), fails = fails.toDouble())
}

@Composable
fun RowScope.AccuracyGraph(success: Double, fails: Double) {
    val defaultValue = @Suppress("MagicNumber") 0.0001f
    Box(
        modifier = Modifier
            .background(Color.Green)
            .fillMaxHeight()//.height(50.dp)
            .weight(
                success
                    .toFloat()
                    .let {
                        return@let if (it <= 0)
                            @Suppress("MagicNumber") defaultValue // cant have weight of 0
                        else
                            it
                    }
            )
            .padding(10.dp),
    )
    Box(
        modifier = Modifier
            .background(Color.Red)
            .height(50.dp)
            .weight(
                fails
                    .toFloat()
                    .let {
                        if (it <= 0)
                            return@let defaultValue
                        else
                            it
                    }
            )
            .padding(10.dp),
    )
}
