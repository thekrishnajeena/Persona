package com.krishnajeena.persona.data_layer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.model.Song
import com.krishnajeena.persona.other.Resource
import com.krishnajeena.persona.services.MusicService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
//class MusicRepository @Inject constructor(
//    private val musicViewModel: MusicViewModel
//) {

interface MusicRepository {
    fun getSongs(): Flow<Resource<List<Song>>>

//    var currentSong = mutableStateOf("")
//
//    private val _isPlaying = MutableLiveData<Boolean>()
//    val isPlaying: LiveData<Boolean> = _isPlaying
//
//    private val _playbackPosition = MutableLiveData<Long>()
//    var playbackPosition: LiveData<Long> = _playbackPosition
//    fun updatePlayState(isPlaying: Boolean) {
//     _isPlaying.postValue(isPlaying)
//    }
//    fun playMusic(context: Context, songUri: Uri) {
//        // Handle music play logic
//        val intent = Intent(context, MusicService::class.java)
//        intent.action = "ACTION_PLAY"
//        intent.putExtra("MUSIC_URI", songUri.toString())
//            context.startForegroundService(intent)
//
//        _isPlaying.postValue(true)
//    }
//
//    fun updatePlaybackPosition(pos: Long) {
//        _playbackPosition.value = pos
//    }
//
//    fun pauseMusic(context: Context) {
//        val intent = Intent(context, MusicService::class.java)
//        intent.action = "ACTION_PAUSE"
//        context.startForegroundService(intent)
//
//        _isPlaying.postValue(false)
//    }

//    fun getSongs(){
//
//       flow{
//           val songs =  musicViewModel.musicList.value
//           val songss = mutableListOf<Song>()
//           if (songs != null) {
//               for(song in songs){
//                   songss.add(Song(song.name,song.path))
//               }
//           }
//
//        if (songs != null) {
//            emit(Resource.Success(songss))
//        }
//       }
//    }

//    companion object {
//        @Volatile
//        private var instance: MusicRepository? = null
//
//        fun getInstance(): MusicRepository {
//            return instance ?: synchronized(this) {
//                instance ?: MusicRepository().also { instance = it }
//            }
//        }
//    }
}
