package com.krishnajeena.persona.screens

import androidx.compose.foundation.background
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.krishnajeena.persona.ui_layer.NoteScreen
import com.krishnajeena.persona.model.NoteViewModel

@Composable
fun NotesScreen() {

    Scaffold(modifier = Modifier.background(Color.White))
    { innerPadding ->

        val viewModel = hiltViewModel<NoteViewModel>()
        val state by viewModel.state.collectAsState()


        NoteScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}