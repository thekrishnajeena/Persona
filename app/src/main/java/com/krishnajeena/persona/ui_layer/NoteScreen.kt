package com.krishnajeena.persona.ui_layer

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.Note
import com.krishnajeena.persona.data_layer.NoteState
import com.krishnajeena.persona.data_layer.OffsetEntity
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.krishnajeena.persona.model.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(state: NoteState, onEvent: (NoteEvent) -> Unit) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = Modifier.padding(bottom = 80.dp).background(Color.White)
    ) { innerPadding ->
        if (showBottomSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()

            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = state.title.value,
                        onValueChange = { state.title.value = it },
                        label = { Text("Note Title") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = state.discription.value,
                        onValueChange = { state.discription.value = it },
                        label = { Text("Note Description") }
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedButton(onClick = {
                        if (state.title.value.isNotBlank() && state.discription.value.isNotBlank()) {
                            onEvent(NoteEvent.SaveNote(state.title.value, state.discription.value))
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showBottomSheet = false
                            }
                        }
                    }) {
                        Text("Add Note", fontSize = 15.sp)
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding(),
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateStartPadding(LayoutDirection.Ltr))) {
            if (state.notes.isEmpty()) {
                Image(
                    painter = painterResource(R.drawable.undraw_taking_notes_re_bnaf),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ZoomableCanvas(
                  viewModel = hiltViewModel<NoteViewModel>(),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFAFAFA))
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomableCanvas(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val notes = state.notes

    val scale = remember { mutableFloatStateOf(1f) }
    val offset = remember { mutableStateOf(Offset.Zero) }
    val selectedNote = remember { mutableStateOf<Note?>(null) }
    val editingNote = remember { mutableStateOf<Note?>(null) }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Bottom Sheet for Editing
    if (editingNote.value != null) {
        ModalBottomSheet(
            onDismissRequest = {
                editingNote.value = null
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        editingNote.value?.let { note ->
                            viewModel.onEvent(NoteEvent.EditNote(
                                note = note,
                                updatedTitle = title,
                                updatedDescription = description
                            ))
                            Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show()
                        }
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                editingNote.value = null
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }
            }
        }
    }

    // Main Canvas
    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                val newScale = (scale.floatValue * zoom).coerceIn(1f, 2.5f)
                scale.floatValue = newScale

                if (newScale > 1f) {
                    val adjustedPan = pan / scale.floatValue
                    val newOffset = offset.value + adjustedPan

                    val scaledWidth = screenWidthPx * newScale
                    val scaledHeight = screenHeightPx * newScale

                    val maxX = (scaledWidth - screenWidthPx) / 2f
                    val maxY = (scaledHeight - screenHeightPx) / 2f

                    offset.value = Offset(
                        x = newOffset.x.coerceIn(-maxX, maxX),
                        y = newOffset.y.coerceIn(-maxY, maxY)
                    )
                } else {
                    offset.value = Offset.Zero
                }
            }
        }.clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = with(density) { screenWidthPx.toDp() },
                    height = with(density) { screenHeightPx.toDp() }
                )
                .graphicsLayer(
                    scaleX = scale.floatValue,
                    scaleY = scale.floatValue,
                    translationX = offset.value.x,
                    translationY = offset.value.y
                )
        ) {
            notes.forEach { note ->
                var position by remember { mutableStateOf(note.position.toOffset()) }

                Box(
                    modifier = Modifier.scale(.8f)
                        .offset {
                            IntOffset(
                                position.x.coerceIn(-30f, screenWidthPx - 130f).toInt(),
                                position.y.coerceIn(-30f, screenHeightPx - 420f).toInt()
                            )
                        }
                        .wrapContentSize()
                        .pointerInput(note.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newPos = position + (dragAmount / scale.floatValue)
                                val clamped = Offset(
                                    newPos.x.coerceIn(-30f, screenWidthPx - 130f),
                                    newPos.y.coerceIn(-30f, screenHeightPx - 420f)
                                )
                                position = clamped
                                viewModel.onEvent(NoteEvent.UpdateNotePosition(note.id, clamped))
                            }
                        }
                        .clickable { selectedNote.value = note }
                ) {
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .wrapContentHeight()
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(3.dp)) {
                            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                note.discription,
                                fontSize = 7.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        title = note.title
                                        description = note.discription
                                        editingNote.value = note
                                    },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(NoteEvent.DeleteNote(note))
                                    },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedNote.value?.let { note ->
            AlertDialog(
                onDismissRequest = { selectedNote.value = null },
                title = { Text(note.title, fontWeight = FontWeight.Bold) },
                text = { Text(note.discription) },
                confirmButton = {
                    TextButton(onClick = { selectedNote.value = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
