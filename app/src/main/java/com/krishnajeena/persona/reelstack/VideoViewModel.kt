package com.krishnajeena.persona.reelstack

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoUriDao: VideoUriDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _videoUris = MutableStateFlow<List<VideoUri>>(emptyList())
    val videoUris: StateFlow<List<VideoUri>> = _videoUris.asStateFlow()

    init {
        viewModelScope.launch {
            videoUriDao.getAllVideos().collect { uris ->
                _videoUris.value = uris
            }
        }
    }



    fun addVideoUri(uri: String) {
        viewModelScope.launch {
            videoUriDao.insertVideo(VideoUri(uri = uri))
        }
    }

    fun removeVideo(videoUri: VideoUri) {
        viewModelScope.launch {
            videoUriDao.deleteVideo(videoUri)
        }
    }
}
