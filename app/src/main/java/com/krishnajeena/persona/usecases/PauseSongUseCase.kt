package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class PauseSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke() = musicController.pause()
}