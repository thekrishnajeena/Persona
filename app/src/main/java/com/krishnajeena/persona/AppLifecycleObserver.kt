package com.krishnajeena.persona

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.krishnajeena.persona.services.MusicService


class AppLifecycleObserver(private val context: Context): DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        // App is destroyed, stop the foreground service
        context.stopService(Intent(context, MusicService::class.java))
    }
}