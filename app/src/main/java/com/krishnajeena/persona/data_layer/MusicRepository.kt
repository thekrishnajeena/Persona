package com.krishnajeena.persona.data_layer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krishnajeena.persona.screens.MusicService
import javax.inject.Inject
class MusicRepository @Inject constructor() {

    var currentSong = mutableStateOf("")

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playbackPosition = MutableLiveData<Long>()
    var playbackPosition: LiveData<Long> = _playbackPosition

    fun playMusic(context: Context, songUri: Uri) {
        // Handle music play logic
        val intent = Intent(context, MusicService::class.java)
        intent.action = "ACTION_PLAY"
        intent.putExtra("MUSIC_URI", songUri.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        _isPlaying.postValue(true)
    }

    fun updatePlaybackPosition(pos: Long) {
        _playbackPosition.value = pos
    }

    fun pauseMusic(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = "ACTION_PAUSE"
        context.startService(intent)

        _isPlaying.postValue(false)
    }


    companion object {
        @Volatile
        private var instance: MusicRepository? = null

        fun getInstance(): MusicRepository {
            return instance ?: synchronized(this) {
                instance ?: MusicRepository().also { instance = it }
            }
        }
    }
}
