package com.krishnajeena.persona.components

import com.krishnajeena.persona.data_layer.Song

sealed class HomeEvent {
    object PlaySong : HomeEvent()
    object PauseSong : HomeEvent()
    object ResumeSong : HomeEvent()
    object FetchSong : HomeEvent()
    data class OnSongSelected(val selectedSong: Song) : HomeEvent()
    data class SetSongs(val songs: List<Song>) : HomeEvent()

}