package com.krishnajeena.persona.ui_layer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
){

    Scaffold(
       floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_noteScreen")
            }){
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }, floatingActionButtonPosition = FabPosition.Center
    ){


        LazyColumn(modifier = Modifier.padding(8.dp)
            .fillMaxSize()
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ,contentPadding = it,){
items(state.notes){
    Row(modifier = Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp)).padding(12.dp)
    , horizontalArrangement = Arrangement.SpaceBetween){
Column{
    Text(text = it.title)
    Text(text = it.discription,
        modifier = Modifier.padding(end = 20.dp))
}
        IconButton(onClick = {onEvent(NoteEvent.DeleteNote(it) )}){
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
        }

    }
}
        }
    }

}