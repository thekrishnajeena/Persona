package com.krishnajeena.persona.model

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraClickViewModel @Inject constructor() : ViewModel() {

    var captureImageTrigger by mutableStateOf(false)
        private set

    var capturedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun triggerCapture() {
        captureImageTrigger = true
    }

    fun resetCaptureTrigger() {
        captureImageTrigger = false
    }

    fun setCapturedImage(uri: Uri) {
        capturedImageUri = uri
    }

    fun clearCapturedImage() {
        capturedImageUri = null
    }
}
