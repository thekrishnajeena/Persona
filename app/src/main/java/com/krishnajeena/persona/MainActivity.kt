package com.krishnajeena.persona

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.reelstack.VideoDatabase
import com.krishnajeena.persona.services.MusicService
import com.krishnajeena.persona.ui_layer.PersonaApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val sharedViewModel: SharedViewModel by viewModels()
    @Inject
    lateinit var database: VideoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycle.addObserver(AppLifecycleObserver(this))

        setContent {
            PersonaApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.destroyMediaController()
        stopService(Intent(this, MusicService::class.java))
    }

}

