package com.kobera.music.violin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kobera.music.violin.feature.tuner.ui.TunerScreen
import com.kobera.music.violin.ui.theme.ViolinAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            ViolinAppTheme {
                setSystemBarColors()
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    TunerScreen()
                }
            }
        }
    }
}


@Composable
fun setSystemBarColors(
    topBarColor: Color = Color.Transparent,
    bottomBarColor: Color = Color.Transparent,
    darkIconsTopBar: Boolean = !isSystemInDarkTheme(),
    darkIconsBottomBar: Boolean = !isSystemInDarkTheme(),
    systemUiController: SystemUiController = rememberSystemUiController()
) {
    //Timber.d("Setting system bar")
    SideEffect {
        systemUiController.setStatusBarColor(color = topBarColor, darkIcons = darkIconsTopBar)
        systemUiController.setNavigationBarColor(
            color = bottomBarColor,
            darkIcons = darkIconsBottomBar
        )
    }
}
