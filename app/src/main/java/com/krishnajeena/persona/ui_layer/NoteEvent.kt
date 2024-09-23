package com.krishnajeena.persona.ui_layer

import com.krishnajeena.persona.data_layer.Note

sealed interface NoteEvent {
    object Sortnotes : NoteEvent
    data class DeleteNote(val note: Note) : NoteEvent
    data class SaveNote(val title:String
    , var discription : String): NoteEvent
}