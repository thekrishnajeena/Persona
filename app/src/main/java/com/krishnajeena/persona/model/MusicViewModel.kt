package com.krishnajeena.persona.model

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krishnajeena.persona.screens.MusicService

class MusicViewModel : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    // LiveData to track the current song URI or title
    private val _currentSong = MutableLiveData<String>()
    val currentSong: LiveData<String> = _currentSong

    private val _currentSongUri = MutableLiveData<Uri>()
    val currentSongUri: LiveData<Uri> = _currentSongUri

    init{
        updatePlayingState(false)
    }

    // Method to update the playing state
    private fun updatePlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    private fun updateCurrentSongUri(songUri: Uri) {
        _currentSongUri.value = songUri
    }

    // Method to update the current song
    private fun updateCurrentSong(songUri: String, context: Context) {
        _currentSong.value =  songUri//getFileName(context.contentResolver, songUri.toUri())
    }

    // Communicate with the service to control music
    fun playMusic(context: Context, songUri: Uri) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = "ACTION_PLAY"
        intent.putExtra("MUSIC_URI", songUri.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        updateCurrentSongUri(songUri)
        updateCurrentSong(songUri.toFile().name, context)
        updatePlayingState(true)
    }

    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }


    fun pauseMusic(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = "ACTION_PAUSE"
        context.startService(intent)
        updatePlayingState(false)
    }

    fun stopMusic(context: Context) {
        val intent = Intent(context, MusicService::class.java)
        intent.action = "ACTION_STOP"
        context.startService(intent)
        updatePlayingState(false)
    }

}