package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.data_layer.Song
import com.krishnajeena.persona.services.MusicController
import javax.inject.Inject

class AddMediaItemsUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) {
        musicController.addMediaItems(songs)
    }
}