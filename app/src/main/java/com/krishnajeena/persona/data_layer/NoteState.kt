package com.krishnajeena.persona.data_layer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class NoteState (
    val notes: List<Note> = emptyList(),
    val title: MutableState<String> = mutableStateOf(""),
  val discription : MutableState<String> = mutableStateOf("")

)