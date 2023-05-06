package com.kobera.music.common.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
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
fun CenteredNavigationBarWithNavigateBackAndRightActionButton(
    modifier: Modifier = Modifier,
    navIconColor: Color = LocalContentColor.current,
    navIconPainter: Painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
    navigator: DestinationsNavigator?,
    rightActionButtonPainter: Painter,
    text: String,
    rightActionButton: () -> Unit,
){
    CenteredNavigationBarWithNavigateBackAndRightActionButton(
        modifier, navIconColor, navIconPainter, navigator, rightActionButtonPainter,
         rightActionButton
    ) { Text(text = text, style = MaterialTheme.typography.headlineMedium) }
}

@Composable
fun CenteredNavigationBarWithNavigateBackAndRightActionButton(
    modifier: Modifier = Modifier,
    navIconColor: Color = LocalContentColor.current,
    navIconPainter: Painter,
    navigator: DestinationsNavigator?,
    rightActionButtonPainter: Painter,
    rightActionButton: () -> Unit,
    navContent: @Composable () -> Unit,
) {
    CenteredNavigationBarWithNavigateBackAndRightActionButton(modifier,
        navIconColor,
        navIconPainter,
        { navigator?.navigateUp() },
        {
            IconButton(onClick =  rightActionButton ) {
                Icon(
                    painter = rightActionButtonPainter,
                    contentDescription = null,
                    tint = navIconColor
                )
            }
        },
        navContent
    )
}

@Composable
fun CenteredNavigationBarWithNavigateBackAndRightActionButton(
    modifier: Modifier = Modifier,
    navIconColor: Color = LocalContentColor.current,
    navIconPainter: Painter,
    navIconAction: () -> Unit,
    rightActionButton: @Composable () -> Unit,
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
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            navContent()
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            rightActionButton()
        }
    }
}

@Composable
fun CenteredNavigationBarWithNavigateBack(
    modifier: Modifier = Modifier.fillMaxWidth().statusBarsPadding(),
    navigator: DestinationsNavigator?,
    backIconColor: Color = LocalContentColor.current,
    label: String
) {
    CenteredNavigationBarWithNavigateBack(
        modifier = modifier,
        navigator = navigator,
        backIconColor = backIconColor
    ) {
        Text(text = label, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun CenteredNavigationBarWithNavigateBack(
    modifier: Modifier = Modifier.fillMaxWidth().statusBarsPadding(),
    navigator: DestinationsNavigator?,
    backIconColor: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    CenteredNavigationBarWithNavigateBack(
        modifier = modifier,
        navigateBack = { navigator?.navigateUp() },
        backIconColor = backIconColor,
        content = content
    )
}

@Composable
fun CenteredNavigationBarWithNavigateBack(
    modifier: Modifier = Modifier.fillMaxWidth().statusBarsPadding(),
    navigateBack: () -> Unit,
    backIconColor: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    CenteredNavigationBarWithNavButton(
        modifier = modifier,
        navIconPainter = rememberVectorPainter(image = Icons.Default.ArrowBack),
        navIconColor = backIconColor,
        navIconAction = navigateBack,
        navContent = content
    )
}

@Composable
fun CenteredNavigationBarWithNavButton(
    modifier: Modifier = Modifier.fillMaxWidth().statusBarsPadding(),
    navIconPainter: Painter,
    navIconColor: Color = LocalContentColor.current,
    navIconAction: () -> Unit,
    navContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        IconButton(onClick = navIconAction) {
            Icon(
                painter = navIconPainter,
                contentDescription = null,
                tint = navIconColor
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            navContent()
        }
    }
}
