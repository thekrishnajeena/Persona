package com.krishnajeena.persona.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import com.krishnajeena.persona.model.CameraPhotoViewModel
import soup.compose.photo.ExperimentalPhotoApi
import soup.compose.photo.PhotoBox
import java.io.File

@OptIn(ExperimentalPhotoApi::class)
@Composable
fun DailyCameraScreen( //viewModel: CameraPhotoViewModel = viewModel()
    navController: NavController
) {
    ClicksScreen(navController)
//    val context = LocalContext.current
//    val navController = rememberNavController()
//
//    // Collect images from the ViewModel as state
//    val clicksUri by viewModel.images.collectAsState()
//
//    // Fetch images when the composable is first displayed
//    LaunchedEffect(Unit) {
//        viewModel.fetchImages(context)
//    }
//
//    NavHost(navController, "personaImagesList") {
//        composable("personaImagesList") {
//            Box(modifier = Modifier.fillMaxSize()) {
//                LazyVerticalGrid(
//                    modifier = Modifier.padding(2.dp),
//                    columns = GridCells.Fixed(2),
//                    contentPadding = PaddingValues(10.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    items(
//                        items = clicksUri,
//                        key = { it.toString() } // Use a unique and stable key for each item
//                    ) { img ->
//                        val dismissState = rememberSwipeToDismissBoxState(
//                            confirmValueChange = {
//                                when (it) {
//                                    SwipeToDismissBoxValue.StartToEnd -> {
//                                        viewModel.removeImage(context, img)
//                                    }
//                                    else -> Unit
//                                }
//                                true
//                            },
//                            positionalThreshold = { it * 0.25f }
//                        )
//
//                        SwipeToDismissBox(
//                            state = dismissState,
//                            modifier = Modifier,
//                            enableDismissFromStartToEnd = true,
//                            enableDismissFromEndToStart = false,
//                            backgroundContent = { DismissBackground(dismissState) },
//                        ) {
//                            Card(
//                                modifier = Modifier.padding(2.dp),
//                                onClick = {
//                                    navController.navigate(
//                                        "openPersonaImage/${Uri.encode(img.toString())}"
//                                    )
//                                },
//                                elevation = CardDefaults.elevatedCardElevation(10.dp),
//                                shape = RoundedCornerShape(10.dp)
//                            ) {
//                                AsyncImage(
//                                    model = img,
//                                    contentDescription = null,
//                                    contentScale = ContentScale.FillWidth,
//                                    modifier = Modifier.fillMaxSize()
//                                )
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//
//        composable(
//            "openPersonaImage/{imageUri}",
//            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
//        )
//        { backStackEntry ->
//            val imageUri = backStackEntry.arguments?.getString("imageUri")
//            if (imageUri != null) {
//                PhotoBox {
//                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
//                }
//            }
//        }
//    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ClicksScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Request and manage camera permission with Accompanist Permissions (make sure dependency is added)
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status !is PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Remember the current lens facing state
    val lensFacing = remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    // Obtain a cameraProvider instance
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    // Define a PreviewView to host the CameraX preview
    val previewView = remember { PreviewView(context) }

    // Bind the camera use case whenever the lensFacing changes
    LaunchedEffect(lensFacing.value) {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = when (lensFacing.value) {
            CameraSelector.LENS_FACING_BACK -> CameraSelector.DEFAULT_BACK_CAMERA
            else -> CameraSelector.DEFAULT_FRONT_CAMERA
        }
        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (exc: Exception) {
            Log.e("ClicksScreen", "Camera binding failed", exc)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status is PermissionStatus.Granted) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                Text("Camera permission is required", fontSize = 16.sp, color = Color.White)
            }
        }

        // Bottom left button: to show clicked images (gallery)
        FloatingActionButton(
            onClick = { navController.navigate("personaImagesList") },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery")
        }

        // Bottom right button: flip camera
        FloatingActionButton(
            onClick = {
                lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK)
                    CameraSelector.LENS_FACING_FRONT
                else
                    CameraSelector.LENS_FACING_BACK
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera")
        }
    }
}

