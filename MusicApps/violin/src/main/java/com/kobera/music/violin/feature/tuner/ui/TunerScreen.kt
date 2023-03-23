package com.kobera.music.violin.feature.tuner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TunerScreen(tunerViewModel: TunerViewModel = getViewModel()) {
    val permission = rememberPermissionState(permission =  android.Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(key1 = Unit){
        permission.launchPermissionRequest()
    }

    when(permission.status){
        is PermissionStatus.Granted -> {
            TunerScreenBody(tunerViewModel = tunerViewModel)
        }
        is PermissionStatus.Denied -> {
            if((permission.status as PermissionStatus.Denied).shouldShowRationale) {
                ShowRationale(permissionState = permission)
            } else {
                OpenSettingsOrRestartApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TunerScreenBody(tunerViewModel: TunerViewModel?){
    Scaffold() { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { tunerViewModel?.startRecording() }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { tunerViewModel?.stopRecording() }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ShowRationale(permissionState: PermissionState) {
    //TODO
    Button(onClick = { permissionState.launchPermissionRequest() }) {
        Text(text = "Allow")
    }
}

@Composable
private fun OpenSettingsOrRestartApp() {
    //TODO
}