package com.krishnajeena.persona.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.ui_layer.AddNoteScreen
import com.krishnajeena.persona.ui_layer.NoteScreen
import com.krishnajeena.persona.ui_layer.NoteViewModel

@Composable
fun NotesScreen(modifier: Modifier = Modifier) {

    Scaffold() { innerPadding ->

        val viewModel = hiltViewModel<NoteViewModel>()
        val state by viewModel.state.collectAsState()


        NoteScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}