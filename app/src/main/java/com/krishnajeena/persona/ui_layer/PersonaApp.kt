package com.krishnajeena.persona.ui_layer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.model.CameraPhotoViewModel
import com.krishnajeena.persona.model.CategoryBlogViewModel
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.other.BottomNavItem

import com.krishnajeena.persona.screens.DailyCameraScreen
import com.krishnajeena.persona.screens.DismissBackground
import com.krishnajeena.persona.screens.ExploreScreen
import com.krishnajeena.persona.screens.MusicScreen
import com.krishnajeena.persona.screens.ReelScreen
import com.krishnajeena.persona.screens.StudyScreen
import com.krishnajeena.persona.screens.ToolsScreen
import com.krishnajeena.persona.screens.WebViewItem
import com.krishnajeena.persona.ui.theme.PersonaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.compose.photo.ExperimentalPhotoApi
import soup.compose.photo.PhotoBox
import java.io.File

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPhotoApi::class)
@Composable
fun PersonaApp(sharedViewModel: SharedViewModel, viewModel: CameraPhotoViewModel = viewModel()) {
    PersonaTheme {
        val navController = rememberNavController()
        var title by remember { mutableStateOf("Persona") }
        val context = LocalContext.current

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
                )
            },
            bottomBar = { BottomBar(navController) },
            floatingActionButtonPosition = FabPosition.Center

        ) { innerPadding ->

            val activity = context as? Activity
            val sharedText = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT)


            val clicksUri by viewModel.images.collectAsState()
//
    // Fetch images when the composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchImages(context)
    }

            val categoryBlogViewModel: CategoryBlogViewModel = viewModel()

            NavHost(navController, startDestination = BottomNavItem.Explore.route, modifier = Modifier
                .padding(innerPadding)) {

                composable(BottomNavItem.Explore.route) { ExploreScreen(onCategoryClick = {
                        blogList ->
                    categoryBlogViewModel.setBlogs(blogList)
                    navController.navigate(BottomNavItem.BlogsOfCategory.route)

                }
                    , navController = navController) }

                composable(BottomNavItem.ReelStack.route) { ReelScreen() }
                composable(BottomNavItem.Clicks.route,
                    deepLinks = listOf(navDeepLink { uriPattern = "app://com.krishnajeena.persona/clicks" })) { DailyCameraScreen(navController) }
                composable(BottomNavItem.Study.route) { StudyScreen() }
                composable(BottomNavItem.Tools.route) { ToolsScreen() }
                composable(BottomNavItem.Music.route){
                    val sharedViewModel: SharedViewModel = hiltViewModel()
                    MusicScreen(sharedViewModel = sharedViewModel)
                }
                composable(
                    route = BottomNavItem.BlogsOfCategory.route
                ) {
                    CategoryClickedScreen(categoryBlogViewModel.selectedBlogs.value)
                }

                composable(
                    route = "webview/{url}",
                    arguments = listOf(navArgument("url") { type = NavType.StringType })
                ) { backStackEntry ->
                    val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                    val url = Uri.decode(encodedUrl) // Decode in case it's encoded
                    WebViewItem(url)
                }

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
        )
        { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            if (imageUri != null) {
                PhotoBox {
                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }
        }

            }

        }
    }
}

@Composable
fun CategoryClickedScreen(value: List<BlogItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(value) { blog ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = blog.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = blog.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }

}
@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.ReelStack,
        BottomNavItem.Clicks,
        BottomNavItem.Study,
        BottomNavItem.Tools
    )
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    var isBottomBarVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val folder = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Persona/Clicks"
    ).apply { if (!exists()) mkdirs() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // success logic
        } else {
            Toast.makeText(context, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    var currentImageUri by remember { mutableStateOf<Uri?>(null) }

    // 👇 Make sure the Box fills the whole screen
    Box(
        modifier = Modifier

    ) {
        // BottomBar section aligned to bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Show/hide with animation
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Box {
                    BottomAppBar(
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        containerColor = Color.White
                    ) {
                        items.forEachIndexed { index, item ->
                            if (index == 2) {
                                Spacer(Modifier.weight(1f)) // leave space for FAB
                            } else {
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = currentRoute == item.route,
                                    onClick = { navController.navigate(item.route) },
                                    alwaysShowLabel = true
                                )
                            }
                        }
                    }

                    // FAB
                    FloatingActionButton(
                        onClick = {
                            if (currentRoute == BottomNavItem.Clicks.route) {
                                val imageFile = File(folder, "IMG_${System.currentTimeMillis()}.jpg")
                                currentImageUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    imageFile
                                )
                                currentImageUri?.let { uri -> cameraLauncher.launch(uri) }
                            } else {
                                navController.navigate(BottomNavItem.Clicks.route) {
                                    launchSingleTop = true
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-32).dp)
                            .size(70.dp),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Clicks",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = { navController.navigate(BottomNavItem.Music.route) },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = if (isBottomBarVisible) 120.dp else 32.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vinyl_disc), // You can use a vinyl or music icon
                contentDescription = "Music",
                modifier = Modifier.size(28.dp)
            )
        }


        // Toggle Button to show/hide BottomBar
        IconButton(
            onClick = { isBottomBarVisible = !isBottomBarVisible },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (isBottomBarVisible) 100.dp else 32.dp)
                .background(Color.White, shape = CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = if (isBottomBarVisible)
                    Icons.Default.KeyboardArrowDown
                else
                    Icons.Default.KeyboardArrowUp,
                contentDescription = if (isBottomBarVisible) "Hide" else "Show"
            )
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
        )
        {

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
