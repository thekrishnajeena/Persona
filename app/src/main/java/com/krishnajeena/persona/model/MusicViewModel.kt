package com.krishnajeena.persona.model

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krishnajeena.persona.data_layer.MusicRepository

//@HiltViewModel
class MusicViewModel : ViewModel() {

    private val musicRepository = MusicRepository.getInstance()
    val isPlaying: LiveData<Boolean> = musicRepository.isPlaying

  //  var playbackPosition: LiveData<Long> = musicRepository.playbackPosition
    // LiveData to track the current song URI or title
    private val _currentSong = MutableLiveData<String>()
    val currentSong: LiveData<String> = _currentSong

    private val _currentSongUri = MutableLiveData<Uri>()
    val currentSongUri: LiveData<Uri> = _currentSongUri

//    init{
//        updatePlayingState(false)
//    }


    // Method to update the playing state
//    private fun updatePlayingState(isPlaying: Boolean) {
//        _isPlaying.value = isPlaying
//    }

    fun updatePlaybackPosition(pos: Long){
        musicRepository.updatePlaybackPosition(pos)
    }

    private fun updateCurrentSongUri(songUri: Uri) {
        _currentSongUri.value = songUri
    }

    // Method to update the current song
    private fun updateCurrentSong(songUri: String) {
        _currentSong.value =  songUri//getFileName(context.contentResolver, songUri.toUri())
    }

    // Communicate with the service to control music
    fun playMusic(context: Context, songUri: Uri, b: Boolean = false) {
        if (b) musicRepository.updatePlaybackPosition(0)
musicRepository.playMusic(context, songUri)
        updateCurrentSongUri(songUri)
        updateCurrentSong(songUri.toFile().name)
     //   updatePlayingState(true)
    }


    fun pauseMusic(context: Context) {
    musicRepository.pauseMusic(context)
    //updatePlayingState(false)
    }

//    fun stopMusic(context: Context) {
//        val intent = Intent(context, MusicService::class.java)
//        intent.action = "ACTION_STOP"
//        context.startService(intent)
//        updatePlayingState(false)
//    }

}