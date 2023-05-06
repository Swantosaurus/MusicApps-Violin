package com.kobera.music.common.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
fun SensitivitySetting(
    modifier: Modifier = Modifier,
    setSensitivity: (Float) -> Unit,
    sensitivity: () -> Float
) {
    Column(modifier.padding(horizontal = 10.dp)) {
        Text(modifier = Modifier.fillMaxWidth(), text = "Sensitivity", textAlign = TextAlign.Center)
        Row(Modifier.fillMaxWidth()) {
            Text(text = "Low")
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "High")
        }
        Slider(
            value = (sensitivity() - 1).absoluteValue,
            onValueChange = {
                setSensitivity(((it - 1).absoluteValue))
            }
        )
    }
}
