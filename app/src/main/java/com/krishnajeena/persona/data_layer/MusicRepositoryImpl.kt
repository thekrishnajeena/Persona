package com.krishnajeena.persona.data_layer

import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.model.Song
import com.krishnajeena.persona.other.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicViewModel: MusicViewModel,
) :
    MusicRepository {
    override fun getSongs() =
        flow {
            val songs = musicViewModel.musicList.value ?: emptyList()
            val songss = songs.map { Song(it.name, it.path) } // More concise
            emit(Resource.Success(songss)) // Directly emit the result
        }

}