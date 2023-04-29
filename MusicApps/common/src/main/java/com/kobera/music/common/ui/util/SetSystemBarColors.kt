package com.kobera.music.common.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Sets the system bar colors.
 *
 * @param topBarColor The color of the top bar.
 * @param bottomBarColor The color of the bottom bar.
 * @param darkIconsTopBar Whether to use dark icons on the top bar.
 * @param darkIconsBottomBar Whether to use dark icons on the bottom bar.
 * @param systemUiController The system UI controller.
 */
@Composable
fun setSystemBarColors(
    topBarColor: Color = Color.Transparent,
    bottomBarColor: Color = Color.Transparent,
    darkIconsTopBar: Boolean = !isSystemInDarkTheme(),
    darkIconsBottomBar: Boolean = !isSystemInDarkTheme(),
    systemUiController: SystemUiController = rememberSystemUiController()
) {
    SideEffect {
        systemUiController.setStatusBarColor(color = topBarColor, darkIcons = darkIconsTopBar)
        systemUiController.setNavigationBarColor(
            color = bottomBarColor,
            darkIcons = darkIconsBottomBar
        )
    }
}
