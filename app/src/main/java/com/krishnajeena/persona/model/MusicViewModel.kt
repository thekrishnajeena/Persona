package com.krishnajeena.persona.model

import android.content.ContentResolver
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class MusicViewModel  @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel()  {

    // Playback state
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying
    private val _currentSong = MutableLiveData<String>()
    val currentSong: LiveData<String> = _currentSong
    private val _currentSongUri = MutableLiveData<Uri>()
    val currentSongUri: LiveData<Uri> = _currentSongUri


    // Music list state
    private val _musicList = MutableLiveData<List<File>>(emptyList())
    val musicList: LiveData<List<File>> get() = _musicList

    private val _currentPosition = MutableLiveData<Long>()
    val currentPosition: LiveData<Long> = _currentPosition
    init {
        loadMusics(context)
    }

    fun updatePlaybackPosition(position: Long) {
        _currentPosition.value = position
    }

    // Call this periodically during playback to sync position
    fun syncPosition(player: MediaPlayer) {
        _currentPosition.value = player.currentPosition.toLong()
    }

    // Music list management
    fun loadMusics(context: Context) {
        viewModelScope.launch {
            val musicDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic")
            if (musicDir.exists()) {
                _musicList.value = musicDir.listFiles()?.toList() ?: emptyList()
            } else {
                _musicList.value = emptyList()
            }
        }
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


    fun addMusic(uri: Uri, context: Context) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val musicDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic")
        if (!musicDir.exists()) musicDir.mkdirs()

        val fileName = getFileName(contentResolver, uri)
        val musicFile = File(musicDir, fileName)

        if (musicFile.exists()) {
            Toast.makeText(context, "This is already in!", Toast.LENGTH_SHORT).show()
            return
        }

        inputStream.use { input ->
            FileOutputStream(musicFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
            }
        }

        loadMusics(context)
    }


    fun updateIsPlaying(isPlaying: Boolean) {
       // _isPlaying.value = isPlaying
    }

    fun removeMusic(music: File) {
        viewModelScope.launch {
            music.delete()
            _musicList.value = _musicList.value?.filter { it != music }
        }
    }

    // Playback controls
    fun playMusic(uri: Uri, context: Context) {
        MusicRepository.getInstance().playMusic(context, uri)
        _currentSongUri.value = uri
        _currentSong.value = uri.toFile().name
    }

    fun pauseMusic(context: Context) {
        MusicRepository.getInstance().pauseMusic(context)
    }
}


//@HiltViewModel
//class MusicViewModel : ViewModel() {
//
//    private val musicRepository = MusicRepository.getInstance()
//    val isPlaying: LiveData<Boolean> = musicRepository.isPlaying
//
//  //  var playbackPosition: LiveData<Long> = musicRepository.playbackPosition
//    // LiveData to track the current song URI or title
//    private val _currentSong = MutableLiveData<String>()
//    val currentSong: LiveData<String> = _currentSong
//
//    private val _currentSongUri = MutableLiveData<Uri>()
//    val currentSongUri: LiveData<Uri> = _currentSongUri
//
////    init{
////        updatePlayingState(false)
////    }
//
//
//    // Method to update the playing state
////    private fun updatePlayingState(isPlaying: Boolean) {
////        _isPlaying.value = isPlaying
////    }
//
//    fun updatePlaybackPosition(pos: Long){
//        musicRepository.updatePlaybackPosition(pos)
//    }
//
//    private fun updateCurrentSongUri(songUri: Uri) {
//        _currentSongUri.value = songUri
//    }
//
//    // Method to update the current song
//    private fun updateCurrentSong(songUri: String) {
//        _currentSong.value =  songUri//getFileName(context.contentResolver, songUri.toUri())
//    }
//
//    // Communicate with the service to control music
//    fun playMusic(context: Context, songUri: Uri, b: Boolean = false) {
//        if (b) musicRepository.updatePlaybackPosition(0)
//musicRepository.playMusic(context, songUri)
//        updateCurrentSongUri(songUri)
//        updateCurrentSong(songUri.toFile().name)
//     //   updatePlayingState(true)
//    }
//
//
//    fun pauseMusic(context: Context) {
//    musicRepository.pauseMusic(context)
//    //updatePlayingState(false)
//    }

//    fun stopMusic(context: Context) {
//        val intent = Intent(context, MusicService::class.java)
//        intent.action = "ACTION_STOP"
//        context.startService(intent)
//        updatePlayingState(false)
//    }

//}