package com.kobera.music.violin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.kobera.music.common.ui.util.setSystemBarColors
import com.kobera.music.violin.feature.NavGraphs
import com.kobera.music.violin.ui.theme.ViolinAppTheme
import com.ramcosta.composedestinations.DestinationsNavHost

/**
 * entry activity point of the app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enable drawing in the status bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ViolinAppTheme {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    setSystemBarColors()
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
private fun MainNavigation() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}


