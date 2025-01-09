package com.krishnajeena.persona.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.Note
import com.krishnajeena.persona.data_layer.NoteDatabase
import com.krishnajeena.persona.ui_layer.NoteEvent
import com.krishnajeena.persona.data_layer.NoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    noteDatabase: NoteDatabase
):  ViewModel() {
    val dao = noteDatabase.notedao
    private val isSortedByDateAdded = MutableStateFlow(true)
    @OptIn(ExperimentalCoroutinesApi::class)
    private var notes = isSortedByDateAdded.flatMapLatest {
        if(it){
            dao.getAllOrderedDataAdded()
        }
        else {
            dao.getAllOrderedTitle()
        }
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(), emptyList()
    )

    val _state = MutableStateFlow(NoteState())
    val state = combine(_state, isSortedByDateAdded, notes){
        state, isSortedByDateAdded, notes ->
        state.copy(
            notes = notes,

        )
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000), NoteState()
    )

    fun onEvent(event: NoteEvent){
        when(event){
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.delete(event.note)
                }
            }
                is NoteEvent.SaveNote -> {
                   // viewModelScope.launch {
                        val note = Note(
                            title = state.value.title.value,
                            discription = state.value.discription.value,
                            dateDated = System.currentTimeMillis()
                        )
                        viewModelScope.launch { dao.upsert(note)
                //    }
                    _state.update {
                        it.copy(title = mutableStateOf(""),
                            discription = mutableStateOf(""))
                    }
                    }
                }

        }
        }
    }

