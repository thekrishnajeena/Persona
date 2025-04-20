package com.krishnajeena.persona.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.krishnajeena.persona.model.CameraClickViewModel

import com.krishnajeena.persona.model.CameraPhotoViewModel
import soup.compose.photo.ExperimentalPhotoApi
import java.io.File
import java.io.FileOutputStream

@Composable
fun DailyCameraScreen(
    navController: NavController,
    viewModel: CameraClickViewModel
) {
    ClicksScreen(navController, viewModel)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ClicksScreen(navController: NavController, viewModel: CameraClickViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status is PermissionStatus.Denied &&
            (cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale
        ) {
            // Optional rationale shown if needed
        } else if (cameraPermissionState.status is PermissionStatus.Denied) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val lensFacing = remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE } }
    val imageCapture = remember { ImageCapture.Builder().build() }

    val captureTrigger = viewModel.captureImageTrigger

    LaunchedEffect(captureTrigger) {
        if (captureTrigger) {
            Log.i("CameraDebug", "Capture Triggered")
            val photoFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val uri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                        viewModel.setCapturedImage(uri)
                        viewModel.resetCaptureTrigger()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                        viewModel.resetCaptureTrigger()
                    }
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (cameraPermissionState.status) {
            is PermissionStatus.Granted -> {
                LaunchedEffect(lensFacing.value) {
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()

                    // Mirror preview if front camera

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing.value)
                        .build()

                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e("Camera", "Failed to bind camera use cases", e)
                    }
                }

                AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            }

            is PermissionStatus.Denied -> {
                val permanentlyDenied = !(cameraPermissionState.status as PermissionStatus.Denied).shouldShowRationale

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Camera permission is required to take photos.",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )

                        if (permanentlyDenied) {
                            Text(
                                text = "Please enable it manually from settings.",
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Button(onClick = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                                context.startActivity(intent)
                            }) {
                                Text("Open Settings")
                            }
                        } else {
                            Button(onClick = {
                                cameraPermissionState.launchPermissionRequest()
                            }) {
                                Text("Allow Camera Permission")
                            }
                        }
                    }
                }
            }
        }

        // Gallery button
        FloatingActionButton(
            onClick = { navController.navigate("personaImagesList") },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 16.dp)
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
        }

        // Flip camera
        FloatingActionButton(
            onClick = {
                lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK)
                    CameraSelector.LENS_FACING_FRONT
                else
                    CameraSelector.LENS_FACING_BACK
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 16.dp)
        ) {
            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera")
        }
        viewModel.capturedImageUri?.let { uri ->
            val flip = lensFacing.value == CameraSelector.LENS_FACING_FRONT

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = if (flip) -1f else 1f
                        },
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 124.dp)
                ) {
                    IconButton(onClick = {
                        viewModel.clearCapturedImage() // ❌ Cancel
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                    }

                    IconButton(onClick = {
                        // ✅ Save to PersonaClicks folder here
                        val folder = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                            File(context.getExternalFilesDir(null), "PersonaClicks")
                        } else {
                            File(context.filesDir, "PersonaClicks")
                        }
                        if (!folder.exists()) folder.mkdirs()

                        val destFile = File(folder, "IMG_${System.currentTimeMillis()}.jpg")

                        val originalBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                        val finalBitmap = if (flip) {
                            val matrix = Matrix().apply { preScale(-1f, 1f) }
                            Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                        } else {
                            originalBitmap
                        }

                        // Save the final bitmap
                        FileOutputStream(destFile).use { out ->
                            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        }

                        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()

                        viewModel.clearCapturedImage()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                    }
                }
            }
        }

    }
}
