package com.krishnajeena.persona.ui_layer

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krishnajeena.persona.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
){

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
       floatingActionButton = {
            FloatingActionButton(onClick = {
                showBottomSheet = true
            }){
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }, floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->


        if (showBottomSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()

            ModalBottomSheet(sheetState = sheetState,
                onDismissRequest = {
                    showBottomSheet = false
                }) {


                Column(
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(value = state.title.value, onValueChange =
                    { state.title.value = it }, label = { Text("Note Title") },
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = state.discription.value, onValueChange =
                    { state.discription.value = it }, label = { Text("Note Description") },
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedButton(onClick = {
                        if (state.title.value.isNotBlank() &&
                            state.discription.value.isNotBlank()
                        ) {
                            onEvent(
                                NoteEvent.SaveNote(
                                    title = state.title.value,
                                    discription = state.discription.value
                                )
                            )
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible)
                                    showBottomSheet = false
                            }
                        }
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            "Add Note", modifier = Modifier,
                            fontFamily = FontFamily.SansSerif, fontSize = 15.sp
                        )
                    }
                }
            }

        }

        if (state.notes.isEmpty()) {
            Image(painter = painterResource(R.drawable.undraw_taking_notes_re_bnaf),
                contentDescription = null, alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.notes) {

                    var expandState by remember { mutableStateOf(false) }

                    Card(modifier = Modifier.fillMaxWidth().padding(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        onClick = { expandState = !expandState }) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp)).padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier) {
                                Text(text = it.title, color = Color.Black, fontSize = 20.sp)

                                Text(
                                    text = it.discription,
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    lineHeight = 14.sp,
                                    maxLines = if (expandState) Int.MAX_VALUE else 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = LinearOutSlowInEasing
                                        )
                                    )
                                )
                            }
                            IconButton(onClick = { onEvent(NoteEvent.DeleteNote(it)) }) {
                                Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                            }

                        }
                    }
                }
            }
        }
    }

}