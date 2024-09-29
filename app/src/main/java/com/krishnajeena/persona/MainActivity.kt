package com.krishnajeena.persona

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.rounded.Note
import androidx.compose.material.icons.rounded.Note
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.screens.BlogsScreen
import com.krishnajeena.persona.screens.BooksScreen
import com.krishnajeena.persona.screens.NotesScreen
import com.krishnajeena.persona.screens.TextsScreen
import com.krishnajeena.persona.ui.theme.PersonaTheme
import com.krishnajeena.persona.ui_layer.NoteScreen
import com.krishnajeena.persona.ui_layer.AddNoteScreen
import com.krishnajeena.persona.ui_layer.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersonaTheme {

                val navController = rememberNavController()
                var title by remember{mutableStateOf("Persona")}

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(title) }
                        )
                    }
                ) {
                    innerPadding ->

                val personaList = listOf(Pair("Notes", R.drawable._282),
                    Pair("Books", R.drawable._920933),
                    Pair("Blogs", R.drawable._1242056),
                    Pair("Texts", R.drawable.comment_7945005))


                NavHost(navController, "mainScreen",
                    Modifier.padding(innerPadding)){

                    composable("mainScreen"){
                        title = "Persona"
                        LazyVerticalGrid(columns = GridCells.Adaptive((LocalConfiguration.current.screenWidthDp/3).dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            contentPadding = PaddingValues(5.dp),
                            modifier = Modifier.padding(10.dp)
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

                    composable("texts"){
                        title = "Texts"
                        TextsScreen()
                    }

                }


                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PersonaItem(name: Pair<String, Int> = Pair("", 0), navController: NavController = rememberNavController()) {

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().height(200.dp)
            .padding(10.dp)){
    Card(modifier = Modifier.fillMaxSize(0.8f),
        onClick = {navController.navigate(name.first.lowercase())}){

        Image(painter = painterResource(name.second),
            contentDescription = null, contentScale = ContentScale.Crop,
            alignment = Alignment.Center)

    }
Text(text = name.first, fontSize = 20.sp)
    }

}


//
//sealed class Screen(){
//
//    @Serializable
//    object NoteScreen
//
//    @Serializable
//    object AddNoteScreen
//
//}