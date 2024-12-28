package com.krishnajeena.persona.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.krishnajeena.persona.model.CameraPhotoViewModel
import soup.compose.photo.ExperimentalPhotoApi
import soup.compose.photo.PhotoBox
import java.io.File

@OptIn(ExperimentalPhotoApi::class)
@Composable
fun DailyCameraScreen(viewModel: CameraPhotoViewModel = viewModel()) {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Collect images from the ViewModel as state
    val clicksUri by viewModel.images.collectAsState()

    // Fetch images when the composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchImages(context)
    }

    NavHost(navController, "personaImagesList") {
        composable("personaImagesList") {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(2.dp),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(
                        items = clicksUri,
                        key = { it.toString() } // Use a unique and stable key for each item
                    ) { img ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                when (it) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        viewModel.removeImage(context, img)
                                    }
                                    else -> Unit
                                }
                                true
                            },
                            positionalThreshold = { it * 0.25f }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier,
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = false,
                            backgroundContent = { DismissBackground(dismissState) },
                        ) {
                            Card(
                                modifier = Modifier.padding(2.dp),
                                onClick = {
                                    navController.navigate(
                                        "openPersonaImage/${Uri.encode(img.toString())}"
                                    )
                                },
                                elevation = CardDefaults.elevatedCardElevation(10.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                AsyncImage(
                                    model = img,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

            }
        }

        composable(
            "openPersonaImage/{imageUri}",
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            if (imageUri != null) {
                PhotoBox {
                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}


fun getImageFromFolder(context: Context): List<Uri>{
    val folder = File(context.getExternalFilesDir(null), "PersonaClicks")
    return if(folder.exists()){
        folder.listFiles()?.filter{it.extension in listOf("jpg", "jpeg", "png") }
            ?.map { Uri.fromFile(it) } ?: emptyList()
    } else return emptyList()
}