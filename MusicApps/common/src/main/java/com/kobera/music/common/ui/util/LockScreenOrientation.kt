package com.kobera.music.common.ui.util

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import timber.log.Timber

/**
 * Locks the screen orientation to a given orientation.
 *
 * @param orientation The orientation to lock the screen to. see [ActivityInfo.SCREEN_ORIENTATION_]
 * @param unlockAfterExitingScreen Whether to unlock the screen orientation after the screen is
 * exited.
 */
@Composable
fun lockScreenOrientation(
    orientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
     unlockAfterExitingScreen: Boolean = true
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        activity.requestedOrientation = orientation
        Timber.d("Starting Orientation Lock $this")
        onDispose {
            Timber.d("Disposing Orientation Lock $this")
            if (unlockAfterExitingScreen) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}
