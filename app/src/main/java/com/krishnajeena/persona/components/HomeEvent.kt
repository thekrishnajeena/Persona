package com.krishnajeena.persona.components

import com.krishnajeena.persona.model.Song

sealed class HomeEvent {
    object PlaySong : HomeEvent()
    object PauseSong : HomeEvent()
    object ResumeSong : HomeEvent()
    object FetchSong : HomeEvent()
    data class OnSongSelected(val selectedSong: Song) : HomeEvent()
}