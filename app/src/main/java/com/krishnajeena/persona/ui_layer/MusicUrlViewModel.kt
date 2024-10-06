package com.krishnajeena.persona.ui_layer

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


class MusicUrlViewModel(context: Context): ViewModel() {

    private val _musicList = MutableLiveData<List<File>>(emptyList())
    val musicList: LiveData<List<File>> get() = _musicList

    init{
        viewModelScope.launch {
            _musicList.value = loadMusics(context)
        }
    }

    fun addMusic(path: Uri, context: Context
                 //artist: String, timeLength: Int, cover: Int
        ){
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(path)

        //viewModelScope.launch{

            val musicDir =
                File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic")
            if (!musicDir.exists()) musicDir.mkdirs()

            val fileName = getFileName(context.contentResolver, path)
            val musicFile = File(musicDir, fileName)
        if(musicFile.exists()){
            Toast.makeText(context, "This is already in!", Toast.LENGTH_SHORT).show()

        }
            else{
            inputStream?.use { input ->
                FileOutputStream(musicFile).use { output ->

                    copyFile(input, output)
                }
        //        }
           }
        }

        _musicList.value = loadMusics(context)
    }


    fun loadMusics(context: Context) : List<File>{
        val musicDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "PersonaMusic")
        if(musicDir.exists()){
                return musicDir.listFiles()?.toList() ?: emptyList()

        }
        return emptyList()
    }


    private fun copyFile(input: InputStream, output: OutputStream) {
   // viewModelScope.launch{
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    //}
    }
    fun removeMusic(music: File){
        viewModelScope.launch{
      music.delete()
            _musicList.value = _musicList.value?.filter{ it != music}
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
