package com.krishnajeena.persona.ui_layer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.persona.R
import com.krishnajeena.persona.screens.BlogsScreen
import com.krishnajeena.persona.screens.BooksScreen
import com.krishnajeena.persona.screens.DailyCameraScreen
import com.krishnajeena.persona.screens.MusicScreen
import com.krishnajeena.persona.screens.NotesScreen
import com.krishnajeena.persona.ui.theme.PersonaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaApp() {
    PersonaTheme {
        val navController = rememberNavController()
        var title by remember { mutableStateOf("Persona") }
        val context = LocalContext.current
        val personaList = remember {
            listOf(
                "Clicks" to R.drawable._8037,
                "Music" to R.drawable.v790_nunny_37,
                "Notes" to R.drawable._282,
                "Books" to R.drawable._920933,
                "Blogs" to R.drawable._1242056
            )
        }

        HandlePermissions(context)

        BackHandler(enabled = true) {
            if (navController.currentDestination?.route != "mainScreen") {
                navController.popBackStack()
            } else {
                (context as? Activity)?.finish()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (navController.currentDestination?.route != "mainScreen") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "mainScreen", Modifier.padding(innerPadding)) {
                composable("clicks") {
                    title = "Clicks"
                    DailyCameraScreen()
                }
                composable("music") {
                    title = "Music"
                    MusicScreen()
                }
                composable("mainScreen") {
                    title = "Persona"
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        contentPadding = PaddingValues(2.dp),
                        modifier = Modifier.padding(3.dp)
                    ) {
                        items(personaList) { item ->
                            PersonaItem(item, navController)
                        }
                    }
                }
                composable("notes") {
                    title = "Notes"
                    NotesScreen()
                }
                composable("books") {
                    title = "Books"
                    BooksScreen()
                }
                composable("blogs") {
                    title = "Blogs"
                    BlogsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonaItem(name: Pair<String, Int>, navController: NavController) {
    val context = LocalContext.current
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            capturedImage = it
            CoroutineScope(Dispatchers.IO).launch {
                saveImageToPersona(context, it)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize(0.8f)
            .height(200.dp)
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .combinedClickable(
                    onClick = {
                        if (name.first == "Clicks") {
                            cameraLauncher.launch(null)
                        } else {
                            navController.navigate(name.first.lowercase())
                        }
                    },
                    onLongClick = {
                        if (name.first == "Clicks") {
                            navController.navigate(name.first.lowercase())
                        }
                    }
                )
        ) {
//            Image(
//                painter = painterResource(name.second),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                alignment = Alignment.Center
//            )
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(name.second)  // The image URL or resource
                    .crossfade(true)  // Enable smooth transition effect when loading
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
        Text(text = name.first, fontSize = 20.sp)
    }
}

@Composable
fun HandlePermissions(context: Context) {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    val arePermissionsGranted = permissions.all { permission ->
        androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
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
}

fun saveImageToPersona(context: Context, capturedImage: Bitmap) {
    val folder = File(context.getExternalFilesDir(null), "PersonaClicks")
    if (!folder.exists()) folder.mkdirs()

    val file = File(folder, "IMG_${System.currentTimeMillis()}.png")
    try {
        FileOutputStream(file).use { outputStream ->
            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
