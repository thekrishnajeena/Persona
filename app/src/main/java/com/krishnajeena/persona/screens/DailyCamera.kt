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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun DailyCameraScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val clicksUri = remember{ getImageFromFolder(context) }

        Box(modifier = Modifier.fillMaxSize(),
        ){

        LazyVerticalGrid(modifier = Modifier.padding(2.dp), columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp), verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(clicksUri){
                img ->

                Card(modifier = Modifier.padding(2.dp),
                    elevation = CardDefaults.elevatedCardElevation(10.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                AsyncImage(model = img, contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize()
                    )
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