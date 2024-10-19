package com.krishnajeena.persona.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.krishnajeena.persona.MainActivity
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.MusicRepository
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MusicService : Service() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var player: ExoPlayer

    var musicRepository: MusicRepository = MusicRepository.getInstance()

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        // Initialize MediaSession
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            isActive = true
            setCallback(object : MediaSessionCompat.Callback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPlay() {
                    super.onPlay()
                    player.playWhenReady = true
                    updateNotification(true)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPause() {
                    super.onPause()
                    player.playWhenReady = false
                    updateNotification(false)
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        // Seek to the saved playback position if available
        musicRepository.playbackPosition.value?.let { player.seekTo(it.toLong()) }

        // Show notification based on playback state
        musicRepository.isPlaying.value?.let { showNotification(uri.toUri().toFile().name, it) }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel_id",
                "Music Playback",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for music playback controls"
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(name: String, isPlaying: Boolean) {
        val remoteViews = RemoteViews(packageName, R.layout.music_notification_controller)
        remoteViews.setTextViewText(R.id.song_title, name)
        remoteViews.setTextViewText(R.id.song_artist, "K J")

        // Update play/pause icon based on playback state
        val playPauseIcon = if (isPlaying) R.drawable.pause1 else R.drawable.icons8_play_94
        remoteViews.setImageViewResource(R.id.play_pause_button, playPauseIcon)

        // PendingIntent for Play/Pause button
        val playPauseIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.play_pause_button, playPausePendingIntent)

        // PendingIntent for Next button
        val nextIntent = Intent(this, MusicService::class.java).apply {
            action = "ACTION_NEXT"
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 1, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.next_button, nextPendingIntent)

        // Build and display the notification
        val notification = NotificationCompat.Builder(this, "music_channel_id")
            .setContentTitle("Playing Music")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContent(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Required for custom view
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(PendingIntent.getActivity(
                this, 0, Intent(this, MusicService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )) // Keep the notification persistent
            .build()

        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(isPlaying: Boolean) {
        musicRepository.currentSong.value?.let { songName ->
            showNotification(songName.toUri().toFile().name, isPlaying)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_PLAY -> {
                    val uri = it.getStringExtra(EXTRA_MUSIC_URI)
                    uri?.let {
                        musicRepository.currentSong.value = uri
                        playMusic(it)
                    }
                }
                ACTION_PAUSE -> {
                    player.pause()
                    musicRepository.updatePlaybackPosition(player.currentPosition)
                }
                ACTION_PLAY_PAUSE -> {
                    if (player.isPlaying) {
                        mediaSession.controller.transportControls.pause()
                        player.pause()
                        musicRepository.updatePlaybackPosition(player.currentPosition)
                    } else {
                        mediaSession.controller.transportControls.play()

                    }
                }
                else -> Unit
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release() // Release MediaSession when the service is destroyed
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val EXTRA_MUSIC_URI = "MUSIC_URI"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
