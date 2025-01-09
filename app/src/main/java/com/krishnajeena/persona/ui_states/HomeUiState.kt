package com.krishnajeena.persona.ui_states

import com.krishnajeena.persona.data_layer.Song

data class HomeUiState(
    val loading: Boolean? = false,
    val songs: List<Song>? = emptyList(),
    val selectedSong: Song? = null,
    val errorMessage: String? = null
)
