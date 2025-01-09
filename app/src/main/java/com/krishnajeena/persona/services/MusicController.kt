package com.krishnajeena.persona.services

import com.krishnajeena.persona.ui_states.PlayerState
import com.krishnajeena.persona.data_layer.Song

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long
    ) -> Unit
    )?

    fun addMediaItems(songs: List<Song>)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun getCurrentSong(): Song?

    fun seekTo(position: Long)
}