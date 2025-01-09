package com.krishnajeena.persona.ui_states

import com.krishnajeena.persona.data_layer.Song

data class MediaControllerUiState(
    val playerState: PlayerState? = null,
    val currentSong: Song? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
)

enum class PlayerState{
    PLAYING,
    PAUSED,
    STOPPED
}