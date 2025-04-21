package com.krishnajeena.persona.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CameraPhotoViewModel : ViewModel() {

    // Mutable state to hold the list of image URIs
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    fun fetchImages(context: Context) {
        val contentResolver = context.contentResolver
        val imageList = mutableListOf<Uri>()

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(
            MediaStore.Downloads._ID,
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.RELATIVE_PATH
        )

        val selection = "${MediaStore.Downloads.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%${Environment.DIRECTORY_DOWNLOADS}/PersonaClicks%")

        val cursor = contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Downloads.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Downloads._ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val uri = ContentUris.withAppendedId(collection, id)
                imageList.add(uri)
            }
        }

        _images.value = imageList
    }

    fun removeImage(context: Context, uri: Uri) {
        try {
            val rowsDeleted = context.contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                Toast.makeText(context, "Image removed successfully.", Toast.LENGTH_SHORT).show()
                _images.value = _images.value.filter { it != uri }
            } else {
                Toast.makeText(context, "Failed to delete the image.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error deleting image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}
