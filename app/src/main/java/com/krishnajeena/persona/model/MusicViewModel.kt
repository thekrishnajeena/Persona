package com.krishnajeena.persona.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.MusicDataSource
import com.krishnajeena.persona.data_layer.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class MusicViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicDataSource: MusicDataSource
) : ViewModel() {

    // Playback state
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private val _currentSong = MutableLiveData<String>()
    val currentSong: LiveData<String> = _currentSong
    private val _currentSongUri = MutableLiveData<Uri>()

    // Music list state
    private val _musicList = MutableLiveData<List<File>>(emptyList())
    val musicList: LiveData<List<File>> get() = _musicList


    private val _currentPosition = MutableLiveData<Long>()

    init {
        loadMusics()
    }

    fun updateMusic(newList: List<Song>) {
        musicDataSource.updateMusicList(newList)
    }

    // Music list management
    private fun loadMusics() {
        viewModelScope.launch {
            val musicDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic")
            _musicList.value = musicDir.listFiles()?.toList() ?: emptyList()
        }

        _musicList.value?.let { it -> updateMusic(it.map { Song(it.name, it.toUri().toString()) }) }
    }

    // Add music to directory
    fun addMusic(uri: Uri) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val musicDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic").apply {
            if (!exists()) mkdirs()
        }

        val fileName = getFileName(contentResolver, uri)
        val musicFile = File(musicDir, fileName)

        if (musicFile.exists()) {
            Toast.makeText(context, "This song is already added!", Toast.LENGTH_SHORT).show()
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

        loadMusics()  // Refresh the music list after adding the new file
    }

    // Get the file name from URI
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex)
                }
            }
        }
        return name
    }

    // Remove music from the list
    fun removeMusic(music: File) {
        viewModelScope.launch {
            if (music.exists()) {
                music.delete()
                _musicList.value = _musicList.value?.filter { it != music }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        _currentPosition.postValue(0)
    }


}
