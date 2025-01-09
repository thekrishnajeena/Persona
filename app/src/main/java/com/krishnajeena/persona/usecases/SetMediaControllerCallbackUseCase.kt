package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.ui_states.PlayerState
import com.krishnajeena.persona.data_layer.Song
import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class SetMediaControllerCallbackUseCase @Inject
constructor(private val musicController: MusicController){
    operator fun invoke(
        callback: (
            playerState: PlayerState,
            currentSong: Song?,
            currentPosition: Long,
            totalDuration: Long
        ) -> Unit
    ) {
        musicController.mediaControllerCallback = callback
    }
}