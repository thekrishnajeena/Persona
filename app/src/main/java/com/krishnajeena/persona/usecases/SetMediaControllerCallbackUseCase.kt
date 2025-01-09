package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.model.PlayerState
import com.krishnajeena.persona.model.Song
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