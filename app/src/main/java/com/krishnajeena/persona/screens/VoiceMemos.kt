package com.krishnajeena.persona.screens

import android.Manifest
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.krishnajeena.persona.R
import com.krishnajeena.persona.features.audio.AudioFile
import com.krishnajeena.persona.features.audio.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun VoiceMemosScreen() {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var recordings by remember { mutableStateOf(listOf<AudioFile>()) }
    var showTooltip by remember { mutableStateOf(false) }
    var permissionToRecordAudio by remember { mutableStateOf(false) }

    permissionToRecordAudio = handleVoicePermissions(context)

    LaunchedEffect(Unit) {
        recordings = audioRecorder.getRecordings()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RecordingsList(recordings, Modifier.fillMaxSize()){ deletedFile ->
            recordings = recordings.filter { it != deletedFile } // Refresh the list after deletion
        }

        FloatingActionButton(
            onClick = {
                showTooltip = true
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Press -> {
                                    if (permissionToRecordAudio) {
                                        isRecording = true
                                        audioRecorder.startRecording()
                                    }
                                }
                                PointerEventType.Release -> {
                                    if (isRecording) {
                                        isRecording = false
                                        try {
                                            audioRecorder.stopRecording()
                                            showSaveDialog = true
                                        } catch (e: RuntimeException) {
                                            Log.e("AudioRecorder", "Error stopping recording", e)
                                            // Handle error gracefully, e.g., delete incomplete file or notify user
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .scale(if (isRecording) 1.2f else 1f), // Animate size change when recording
            containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary // Change color when recording
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }


        if (showTooltip) {
            TooltipBox(
                text = "Hold to record, release to stop",
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp)
            )
            LaunchedEffect(showTooltip) {
                delay(2000)
                showTooltip = false
            }
        }
    }

    if (showSaveDialog) {
        SaveAudioDialog(
            onDismiss = {
                showSaveDialog = false
                audioRecorder.discardRecording()
            },
            onSave = { fileName ->
                audioRecorder.saveRecording(fileName).let {
                    recordings = audioRecorder.getRecordings()
                    Toast.makeText(context, "Audio saved successfully", Toast.LENGTH_SHORT).show()
                }
                showSaveDialog = false
            }
        )
    }
}

@Composable
fun RecordingsList(recordings: List<AudioFile>, modifier: Modifier = Modifier, onDelete: (AudioFile) -> Unit) {
    if(recordings.isEmpty()){
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
//     AsyncImage(model = R.drawable._264828, contentDescription = "No Voice Memos!",)
            Text("No recorded memos! Hold mic to record one.", fontSize = 18.sp)
        }
    }
    else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(recordings) { file ->
                RecordingItem(file) {
                    onDelete(file)
                }
            }
        }
    }
}

@Composable
fun RecordingItem(file: AudioFile, onDelete: () -> Unit) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth().padding(2.dp)
    , elevation = CardDefaults.cardElevation(5.dp),
        onClick = {
            val fileUri = file.uri

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "audio/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app found to play audio", Toast.LENGTH_SHORT).show()
            }

        })
    {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        , horizontalArrangement = Arrangement.SpaceBetween
        , verticalAlignment = Alignment.CenterVertically
    ) {
        var duration by remember { mutableStateOf(0L) }

        LaunchedEffect(file) {
            duration = getAudioDurationAsync(context, file.uri)
        }
        Text(text = file.name, fontSize = 18.sp, textAlign = TextAlign.Center)
        Text(text = formatDuration(duration), fontSize = 14.sp, textAlign = TextAlign.Center)

        IconButton(onClick = {
            try {
                // Attempt to delete the file using ContentResolver
                val deletedRows = context.contentResolver.delete(
                    file.uri, // Use file.uri if you're working with MediaStore URIs
                    null,
                    null
                )

                if (deletedRows > 0) {
                    Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                    onDelete() // Notify parent to refresh the list
                } else {
                    Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
                }
            } catch (securityException: SecurityException) {
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException
                recoverableSecurityException?.userAction?.actionIntent?.intentSender?.let { intentSender ->
                    // Launch a request to get user permission to delete the file
                    val activity = context as? Activity
                    activity?.startIntentSenderForResult(
                        intentSender,
                        DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete audio")
        }


    }
    }
}

const val DELETE_REQUEST_CODE = 1001

suspend fun getAudioDurationAsync(context: Context, uri: Uri): Long = withContext(Dispatchers.IO) {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(context, uri)
    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    durationStr?.toLong() ?: 0
}

fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun TooltipBox(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun SaveAudioDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Audio") },
        text = {
            Column {
                Text("Enter a name for your audio file:")
                TextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    ".mp3",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fileName) },
                enabled = fileName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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

