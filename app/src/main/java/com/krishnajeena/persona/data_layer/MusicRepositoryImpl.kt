package com.krishnajeena.persona.data_layer

import com.krishnajeena.persona.MusicDataSource
import com.krishnajeena.persona.other.Resource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val musicDataSource: MusicDataSource,
) :
    MusicRepository {
    override fun getSongs() =
        flow {
            val songs = musicDataSource.musicList.firstOrNull() ?: emptyList()
            val songss = songs.map { Song(it.title, it.songUrl) }
            emit(Resource.Success(songss))
        }

}