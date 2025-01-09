package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class GetCurrentSongPositionUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke() = musicController.getCurrentPosition()
}