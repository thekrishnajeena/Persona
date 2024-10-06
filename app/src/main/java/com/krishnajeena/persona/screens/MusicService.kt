package com.krishnajeena.persona.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.krishnajeena.persona.MainActivity
import com.krishnajeena.persona.R

class MusicService : Service() {

    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(uri: String){
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        showNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(){
        val remoteViews = RemoteViews(packageName, R.layout.music_notification_controller)
        remoteViews.setTextViewText(R.id.song_title, "Persona Music")
        remoteViews.setTextViewText(R.id.song_artist, "K J")

        val playPauseIntent = Intent(this, MusicService::class.java).apply {
            action  = "ACTION_PLAY_PAUSE"
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.play_pause_button, playPausePendingIntent)

        // Handle next button click
        val nextIntent = Intent(this, MusicService::class.java).apply {
            action = "ACTION_NEXT"
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 1, nextIntent, PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.next_button, nextPendingIntent)

        // Build the notification
        val notification = NotificationCompat.Builder(this, "music_channel_id")
            .setSmallIcon(R.drawable.chat_7945263)
            .setContent(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Required for custom view
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Keep the notification persistent
            .build()

startForeground(1, notification)
//        val channelId = "music_channel_id"
//        val channelName = "Music Service"
//        val notificationManager = getSystemService(NotificationManager::class.java)
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val intent = Intent(this, MusicService::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Music Service")
//            .setContentText("Playing music")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentIntent(pendingIntent)
//            .addAction(R.drawable.chat_7945263, "Pause", getActionPendingIntent(ACTION_PAUSE))
//            .addAction(R.drawable.chat_7945263, "Play", getActionPendingIntent(ACTION_PLAY))
//            .setOngoing(true)
//            .build()
//
//        startForeground(1, notification)

    }

    private fun getActionPendingIntent(action: String): PendingIntent? {
        val intent = Intent(this, MusicService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let{
            when(it.action){
                ACTION_PLAY ->{
                    val uri = it.getStringExtra(EXTRA_MUSIC_URI)
                    uri?.let{

                        playMusic(it)
                    }
                }
                ACTION_PAUSE ->{
                    player.pause()
                }
                ACTION_PLAY_PAUSE -> {
                    if(player.isPlaying) player.pause()

                    else {
                        val uri = it.getStringExtra(EXTRA_MUSIC_URI)
                        uri?.let{playMusic(it)}
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