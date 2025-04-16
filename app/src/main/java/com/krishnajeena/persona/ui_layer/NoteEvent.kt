package com.krishnajeena.persona.ui_layer

import androidx.compose.ui.geometry.Offset
import com.krishnajeena.persona.data_layer.Note

sealed interface NoteEvent {
    data class DeleteNote(val note: Note) : NoteEvent
    data class SaveNote(val title:String
    , var discription : String): NoteEvent

    data class UpdateNotePosition(val id: Int, val newOffset: Offset) : NoteEvent
    data class UpdateNoteSize(val id: Int, val newSize: Offset) : NoteEvent
    data class EditNote(val note: Note) : NoteEvent
}