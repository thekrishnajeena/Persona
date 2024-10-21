package com.krishnajeena.persona

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.screens.BlogsScreen
import com.krishnajeena.persona.screens.BooksScreen
import com.krishnajeena.persona.screens.DailyCameraScreen
import com.krishnajeena.persona.screens.MusicScreen
import com.krishnajeena.persona.screens.NotesScreen
import com.krishnajeena.persona.screens.TextsScreen
import com.krishnajeena.persona.ui.theme.PersonaTheme
import com.krishnajeena.persona.ui_layer.NoteScreen
import com.krishnajeena.persona.ui_layer.AddNoteScreen
import com.krishnajeena.persona.ui_layer.BlogUrlViewModel
import com.krishnajeena.persona.ui_layer.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycle.addObserver(AppLifecycleObserver(this))
//        val app = application as BaseClass
//        val blogUrlViewModel = ViewModelProvider(this)[BlogUrlViewModel::class.java]

        setContent {
            PersonaTheme {

                val navController = rememberNavController()
                var title by remember{mutableStateOf("Persona")}

                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                val context = LocalContext.current

                // Permissions to request
                val permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) // WRITE_EXTERNAL_STORAGE is deprecated in Android 10+
                }

                // Launches permission request dialog
                val requestPermissionsLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissionsMap ->
                    val allGranted = permissionsMap.all { it.value }
                    if (allGranted) {
                        Toast.makeText(context, "All Permissions Granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Permissions Denied", Toast.LENGTH_SHORT).show()
                    }
                }

                // Check if permissions are already granted
                val arePermissionsGranted = permissions.all { permission ->
                    androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                }

                // If permissions are not granted, request them at the start of the app
                LaunchedEffect(Unit) {
                    if (!arePermissionsGranted) {
                        requestPermissionsLauncher.launch(permissions)
                    }
                }

                BackHandler(enabled =  true){

                    if(navController.currentDestination?.route != "mainScreen"){
                        navController.popBackStack()
                    } else{
                        (context as? Activity)?.finish()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if(currentRoute != "mainScreen") {
                                    IconButton(onClick = { //navController.navigateUp()

                                        navController.popBackStack()}){

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = null
                                        )
                                    }
                                } else null
                            }
                        )
                    },


                ) {
                    innerPadding ->

                val personaList = listOf(
                    Pair("Clicks", R.drawable._8037),
                    Pair("Music", R.drawable.v790_nunny_37),
                    Pair("Notes", R.drawable._282),
                    Pair("Books", R.drawable._920933),
                    Pair("Blogs", R.drawable._1242056),
                //    Pair("Texts", R.drawable.msg)
                )


                NavHost(navController, "mainScreen",
                    Modifier.padding(innerPadding)){

                    composable("clicks"){

                        title = "Clicks"
                        DailyCameraScreen()

                    }

                    composable("music"){
                    MusicScreen()
                    }

                    composable("mainScreen"){
                        title = "Persona"
                        LazyVerticalGrid(columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.Center,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            contentPadding = PaddingValues(2.dp),
                            modifier = Modifier.padding(3.dp)

                        ) {
                            items(personaList){
                                    item ->
                                PersonaItem(item, navController)
                            }
                        }
                    }

                    composable("notes"){

                        title = "Notes"
                        NotesScreen()

                    }

                    composable("books"){

                        title = "Books"
                        BooksScreen()

                    }

                    composable("blogs"){

                        title = "Blogs"
                        BlogsScreen()

                    }

//                    composable("texts"){
//                        title = "Texts"
//                        TextsScreen()
//                    }

                }


                }

            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonaItem(name: Pair<String, Int> = Pair("", 0), navController: NavController = rememberNavController()) {

    val context = LocalContext.current
    var navB by remember { mutableStateOf(false) }

    if(navB){
        LaunchedEffect(Unit) {
            navController.navigate(name.first.lowercase())
            navB = false
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().height(200.dp)
            .padding(5.dp)){
        if(name.first == "Clicks"){
            var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) {
                    bitmap ->
                if(bitmap != null){
                    capturedImage = bitmap
                    saveImageToPersona(context, capturedImage!!)
                }
            }
            Card(modifier = Modifier.fillMaxSize(0.8f)
                .combinedClickable (
                    onClick = {
                       cameraLauncher.launch(null)
                              }
                    , onLongClick = {
                        navController.navigate(name.first.lowercase())
                    }
                )

            ){

                Image(painter = painterResource(name.second),
                    contentDescription = null, contentScale = ContentScale.Crop,
                    alignment = Alignment.Center)

            }
            Text(text = name.first, fontSize = 20.sp)

        }
        else{
    Card(modifier = Modifier.fillMaxSize(0.8f),
        onClick = {

        //    navController.navigate(name.first.lowercase())
            navB = true

        }){

        Image(painter = painterResource(name.second),
            contentDescription = null, contentScale = ContentScale.Crop,
            alignment = Alignment.Center)

    }
Text(text = name.first, fontSize = 20.sp)
    }
        }

}

fun saveImageToPersona(context: Context, capturedImag: Bitmap) {

    val capturedImage = Bitmap.createScaledBitmap(capturedImag,
        capturedImag.width*2, capturedImag.height*2, true)
    val folder = File(context.getExternalFilesDir(null), "PersonaClicks")
    if(!folder.exists()){
        folder.mkdirs()
    }

    val filename = "IMG_${System.currentTimeMillis()}.png"
    val file = File(folder, filename)

    try {
        val outputStream = FileOutputStream(file)
        capturedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
    } catch (e: IOException){
        e.printStackTrace()
    }

}
