package com.kobera.music.common.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController


/**
 * sets colors of system bars
 * has to be in main module to work properly
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