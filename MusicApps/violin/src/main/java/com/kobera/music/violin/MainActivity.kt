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
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kobera.music.violin.feature.NavGraphs
import com.kobera.music.violin.feature.tuner.ui.TunerScreen
import com.kobera.music.violin.ui.theme.ViolinAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enable drawing in the status bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ViolinAppTheme {
                //setSystemBarColors()
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {

                    MainNavigation()
                    //TunerScreen()
                }
            }
        }
    }
}

@Composable
private fun MainNavigation() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}


