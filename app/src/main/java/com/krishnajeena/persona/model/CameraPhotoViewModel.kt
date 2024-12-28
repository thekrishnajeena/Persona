package com.krishnajeena.persona.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import java.io.File

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
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
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Storage permission required!", Toast.LENGTH_SHORT).show()
            _images.value = emptyList()
            return
        }

        val folder = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File(context.getExternalFilesDir(null), "PersonaClicks")
        } else {
            File(context.filesDir, "PersonaClicks")
        }

        _images.value = if (folder.exists()) {
            folder.listFiles()
                ?.filter { it.extension in listOf("jpg", "jpeg", "png") }
                ?.map { Uri.fromFile(it) } ?: emptyList()
        } else {
            Toast.makeText(context, "Folder not found!", Toast.LENGTH_SHORT).show()
            emptyList()
        }
    }

    fun removeImage(context: Context, uri: Uri) {
        val file = File(uri.path ?: "")
        if (file.exists() && file.delete()) {
            Toast.makeText(context, "Image removed successfully.", Toast.LENGTH_SHORT).show()
            _images.value = _images.value.filter { it != uri }
        } else {
            Toast.makeText(context, "Failed to delete the image.", Toast.LENGTH_SHORT).show()
        }
    }
}
