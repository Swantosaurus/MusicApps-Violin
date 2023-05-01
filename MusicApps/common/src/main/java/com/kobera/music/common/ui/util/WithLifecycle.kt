package com.kobera.music.common.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * activity lifecycle observer in Compose
 */
@Composable
fun withLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onStart: () -> Unit = {},
    onCreate: () -> Unit = {},
    onResume: () -> Unit = {},
    onStop: () -> Unit = {},
    onPause: () -> Unit = {},
    onDestroy: () -> Unit = {},
    onAny: () -> Unit = {}
) {
    val currentOnStart by rememberUpdatedState(newValue = onStart)
    val currentOnCreate by rememberUpdatedState(newValue = onCreate)
    val currentOnResume by rememberUpdatedState(newValue = onResume)
    val currentOnStop by rememberUpdatedState(newValue = onStop)
    val currentOnPause by rememberUpdatedState(newValue = onPause)
    val currentOnDestroy by rememberUpdatedState(newValue = onDestroy)
    val currentOnAny by rememberUpdatedState(newValue = onAny)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _ , event ->
            when (event) {
                Lifecycle.Event.ON_START -> currentOnStart()
                Lifecycle.Event.ON_CREATE -> currentOnCreate()
                Lifecycle.Event.ON_RESUME -> currentOnResume()
                Lifecycle.Event.ON_STOP -> currentOnStop()
                Lifecycle.Event.ON_PAUSE -> currentOnPause()
                Lifecycle.Event.ON_DESTROY -> currentOnDestroy()
                Lifecycle.Event.ON_ANY -> currentOnAny()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}