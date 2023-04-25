package com.kobera.music.common.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun CenteredNavigationBarWithNavigateBack(
    navigator: DestinationsNavigator?,
    backIconColor: Color = LocalContentColor.current,
    label: String
) {
    CenteredNavigationBarWithNavigateBack(navigator = navigator, backIconColor = backIconColor) {
        Text(text = label, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun CenteredNavigationBarWithNavigateBack(
    navigator: DestinationsNavigator?,
    backIconColor : Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    CenteredNavigationBarWithNavigateBack(
        navigateBack = { navigator?.navigateUp() },
        backIconColor = backIconColor,
        content = content
    )
}

@Composable
fun CenteredNavigationBarWithNavigateBack(
    navigateBack: () -> Unit,
    backIconColor : Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    CenteredNavigationBarWithNavButton(
        navIconPainter = rememberVectorPainter(image = Icons.Default.ArrowBack),
        navIconColor = backIconColor,
        navIconAction = navigateBack,
        navContent = content
    )
}

@Composable
fun CenteredNavigationBarWithNavButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier.fillMaxWidth(),
    navIconPainter : Painter,
    navIconColor : Color = LocalContentColor.current,
    navIconAction : () -> Unit,
    navContent: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        IconButton(onClick = navIconAction) {
            Icon(
                painter = navIconPainter,
                contentDescription = null,
                tint = navIconColor
            )
        }
        Row(
            Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            navContent()
        }
    }
}