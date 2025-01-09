package com.krishnajeena.persona.model

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