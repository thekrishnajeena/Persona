package com.krishnajeena.persona.ui_layer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.persona.R
import com.krishnajeena.persona.model.SharedViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaApp(sharedViewModel: SharedViewModel) {
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
                        val backStackEntry by navController.currentBackStackEntryAsState()


                        if (backStackEntry?.destination?.route != "mainScreen") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "mainScreen", Modifier.padding(innerPadding)) {
                composable("clicks",
                    deepLinks = listOf(navDeepLink { uriPattern = "app://com.krishnajeena.persona/clicks" })
                ) {
                    title = "Clicks"
                    DailyCameraScreen()
                }
                composable("music") {
                    title = "Music"
                    MusicScreen(sharedViewModel = sharedViewModel)
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

    val folder = File(context.getExternalFilesDir(null), "PersonaClicks")
    if (!folder.exists()) folder.mkdirs()

// Create a file and corresponding Uri
    val imageFile = File(folder, "IMG_${System.currentTimeMillis()}.jpg")
    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

// Updated camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Handle the full-resolution image stored at `imageUri`
            CoroutineScope(Dispatchers.IO).launch {
                saveImageToPersonaUri(context, imageUri) // Optional custom save logic
            }
        } else {
            Toast.makeText(context, "Image capture failed", Toast.LENGTH_SHORT).show()
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
                            cameraLauncher.launch(imageUri)
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
fun saveImageToPersonaUri(context: Context, imageUri: Uri) {
    val contentResolver = context.contentResolver

    try {
        val inputStream = contentResolver.openInputStream(imageUri)
        inputStream?.use {
            // Optionally read or validate the image content
        }

        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Image saved to PersonaClicks!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Failed to save the image!", Toast.LENGTH_SHORT).show()
        }
    }
}
