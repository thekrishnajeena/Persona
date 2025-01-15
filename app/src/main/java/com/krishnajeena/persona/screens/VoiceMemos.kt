package com.krishnajeena.persona.screens

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.krishnajeena.persona.ui_layer.HandlePermissions

@Composable
fun VoiceMemosScreen(modifier: Modifier = Modifier) {

    var permissinToRecordAudio by remember{mutableStateOf(false)}

    val context = LocalContext.current
    //LaunchedEffect(handleVoicePermissions(context)) {
        permissinToRecordAudio = handleVoicePermissions(context)
    //}

    Scaffold(floatingActionButton = {
    FloatingActionButton(onClick = {

    }) {
        Icon(imageVector= Icons.Filled.PlayArrow, contentDescription = "Record")
    }

    })
     { innerPadding ->

        LazyColumn(modifier = modifier.fillMaxSize()
            .padding(innerPadding)) {



        }

    }
}

@Composable
fun handleVoicePermissions(context: Context):Boolean{
    var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    val arePermissionsGranted = permissions.all { permission ->
        androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    val requestPermissionsLauncher  = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
        val allGranted = permissionsMap.all { it.value }
        Toast.makeText(
            context,
            if (allGranted) "All Permissions Granted" else "Permissions Denied",
            Toast.LENGTH_SHORT
        ).show()
    }
    LaunchedEffect(Unit) {
        if (!arePermissionsGranted) {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    return arePermissionsGranted
    }

