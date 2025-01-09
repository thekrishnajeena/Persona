package com.krishnajeena.persona

import com.krishnajeena.persona.data_layer.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MusicDataSource @Inject constructor() {
    private val _musicList = MutableStateFlow<List<Song>>(emptyList())
    val musicList: StateFlow<List<Song>> get() = _musicList

    fun updateMusicList(newList: List<Song>) {
        _musicList.value = newList
    }
}
