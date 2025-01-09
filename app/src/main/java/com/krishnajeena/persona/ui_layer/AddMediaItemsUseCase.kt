package com.krishnajeena.persona.ui_layer

import com.krishnajeena.persona.model.Song
import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class AddMediaItemsUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        musicController.addMediaItems(songs)
    }
}