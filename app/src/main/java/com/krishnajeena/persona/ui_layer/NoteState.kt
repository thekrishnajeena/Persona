package com.krishnajeena.persona.ui_layer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.krishnajeena.persona.data_layer.Note

data class NoteState (
    val notes: List<Note> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
  val discription : MutableState<String> = mutableStateOf("")

)