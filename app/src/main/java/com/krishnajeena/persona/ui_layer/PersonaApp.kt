package com.krishnajeena.persona.ui_layer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Velocity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.krishnajeena.persona.R
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.screens.BlogsScreen
import com.krishnajeena.persona.screens.BooksScreen
import com.krishnajeena.persona.screens.DailyCameraScreen
import com.krishnajeena.persona.screens.MusicScreen
import com.krishnajeena.persona.screens.NotesScreen
import com.krishnajeena.persona.screens.ToolsScreen
import com.krishnajeena.persona.screens.VoiceMemosScreen
import com.krishnajeena.persona.ui.theme.PersonaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                "Blogs" to R.drawable._1242056,
                "Voice" to R.drawable._209989
               // , "Tools" to R.drawable.reelstack
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

        val pullOffset = remember { androidx.compose.animation.core.Animatable(0f) }
        val scope = rememberCoroutineScope()

        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    return Offset.Zero
                }

                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                    // Update pullOffset smoothly while dragging down
                    scope.launch {
                        val newOffset = (pullOffset.value + available.y * 0.5f).coerceAtLeast(0f)
                        pullOffset.snapTo(newOffset)
                    }
                    return Offset.Zero
                }

                override suspend fun onPreFling(available: androidx.compose.ui.unit.Velocity): androidx.compose.ui.unit.Velocity {
                    if (pullOffset.value > 300f) {  // Threshold reached → Navigate to "tools"
                        navController.navigate("tools")
                    }

                    // Always reset pullOffset after release
                    scope.launch {
                        pullOffset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
                    }

                    return super.onPreFling(available)
                }
            }
        }

        // Gesture-based pull handling
        val pullGestureModifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume() // Consume touch event
                    scope.launch {
                        val newOffset = (pullOffset.value + dragAmount.y * 0.5f).coerceAtLeast(0f)
                        pullOffset.snapTo(newOffset) // Update animation instantly
                    }
                },
                onDragEnd = {
                    scope.launch {
                        if (pullOffset.value < 300f) { // Reset if not enough pull
                            pullOffset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
                        }
                    }
                }
            )
        }

        // Reset animation when navigation happens
        LaunchedEffect(navController.currentBackStackEntry) {
            scope.launch {
                pullOffset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
            }
        }

// Apply this modifier to the Box or Column containing the pull effect



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
            NavHost(navController, startDestination = "mainScreen", Modifier.padding(innerPadding)
                ) {
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

                composable("mainScreen"){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(nestedScrollConnection)
                            .then(pullGestureModifier)

                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                            ,
                            horizontalAlignment = Alignment.CenterHorizontally,

                        ) {
                            // Stretchy Pull effect at the top
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(pullOffset.value.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Animated Arrow
                                val rotationAngle by animateFloatAsState(
                                    targetValue = if (pullOffset.value > 100f) 180f else 0f,
                                    animationSpec = tween(durationMillis = 200)
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "Pull to Open",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .rotate(rotationAngle),
                                        tint = Color.Gray
                                    )
                                    Text("Pull ReelStack", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Persona content with LazyVerticalGrid
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
                composable("voice"){
                    title = "Voice"
                    VoiceMemosScreen()
                }

                composable("tools"){
                    title="ReelStack" //tools to be made later when there are tools
                    ToolsScreen()

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
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
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
