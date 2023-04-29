package com.kobera.music.common.ui.component

import android.Manifest.permission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

/**
 *  Wrapper for the permission composable that handles the different states of the permission.
 *
 *  @param permissionGranted: The composable to show when the permission is granted.
 *  @param showRationale: The composable to show when the permission is denied but the user still can request it.
 *  @param permissionDenied: The composable to show when the permission is denied and the user can't request it.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleAudioPermission(
    permissionGranted: @Composable () -> Unit,
    showRationale: @Composable (PermissionState) -> Unit,
    permissionDenied: @Composable () -> Unit,
) {
    val permission = rememberPermissionState(permission = permission.RECORD_AUDIO)

    LaunchedEffect(key1 = Unit) {
        permission.launchPermissionRequest()
    }

    when (permission.status) {
        is PermissionStatus.Granted -> {
            permissionGranted()
        }

        is PermissionStatus.Denied -> {
            if((permission.status as PermissionStatus.Denied).shouldShowRationale) {
                showRationale(permission)
            } else {
                permissionDenied()
            }
        }
    }
}
