package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(private  val musicController: MusicController) {
    operator fun invoke(mediaItemIndex: Int) {
        musicController.play(mediaItemIndex)
    }
}