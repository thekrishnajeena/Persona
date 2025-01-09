package com.krishnajeena.persona.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.PlaybackState
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media.session.MediaButtonReceiver
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.MusicRepository
import com.krishnajeena.persona.model.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    private lateinit var player: ExoPlayer

   // @Inject
    lateinit var musicViewModel: MusicViewModel
    private lateinit var playbackStateObserver: Observer<Boolean>

    var musicRepository: MusicRepository = MusicRepository.getInstance()


    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(MediaSessionCallback())
            .build()

//        player = ExoPlayer.Builder(this).build()
//      //  musicViewModel = viewModel()
//        Log.i("TAG", "Inside service: ${musicViewModel.toString()}")
//       mediaSession = MediaSessionCompat(this, "MusicService").apply {
//            isActive = true
//            setCallback(object : MediaSessionCompat.Callback() {
//                override fun onPlay() {
//                    super.onPlay()
//                    player.playWhenReady = true
//                    updateNotification(true)
//                    musicViewModel.updateIsPlaying(true)  // Update playback state in ViewModel
//                }
//
//                override fun onPause() {
//                    super.onPause()
//                    player.playWhenReady = false
//                    updateNotification(false)
//                    musicViewModel.updateIsPlaying(false)  // Update playback state in ViewModel
//                }
//
//            })
//        }



        // Initialize MediaSession
       // musicViewModel = MusicViewModel(this)

        // Initialize MediaSession
//        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//            .setAudioAttributes(
//                android.media.AudioAttributes.Builder()
//                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
//                    .build())
//            .setOnAudioFocusChangeListener { focusChange ->
//                when (focusChange) {
//                    AudioManager.AUDIOFOCUS_LOSS -> {
//                        player.playWhenReady = false
//                        updateNotification(false)
//                        musicViewModel.updateIsPlaying(false)
//                    }
//                    AudioManager.AUDIOFOCUS_GAIN -> {
//                        player.playWhenReady = true
//                        updateNotification(true)
//                        musicViewModel.updateIsPlaying(true)
//                    }
//                }
//            }
//            .build()
//
//        audioManager.requestAudioFocus(audioFocusRequest)
//
//        // Listen for playback state changes in the ViewModel
//        playbackStateObserver = Observer { isPlaying ->
//            if (isPlaying) {
//                player.playWhenReady = true
//            } else {
//                player.playWhenReady = false
//            }
//        }
//        musicViewModel.isPlaying.observe(this, playbackStateObserver)


}

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession


    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }

        super.onDestroy()
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val updatedMediaItems = mediaItems.map {
                it.buildUpon().setUri(it.mediaId).build()
            }.toMutableList()

            return Futures.immediateFuture(updatedMediaItems)
        }
    }

//    private fun playMusic(uri: String) {
//        val mediaItem = MediaItem.fromUri(uri)
//
//        player.setMediaItem(mediaItem)
//        player.prepare()
//        player.playWhenReady = true
//
//        mediaSession.controller.transportControls.play()
//
//        // Seek to the saved playback position if available
//        musicRepository.playbackPosition.value?.let { player.seekTo(it) }
//
//        // Show notification based on playback state
//        musicRepository.isPlaying.value?.let { showNotification() }
//       // createNotificationChannel()
//
//    }
//
//    private fun createNotificationChannel() {
//
//            val channel = NotificationChannel(
//                "music_channel_id",
//                "Music Playback",
//                NotificationManager.IMPORTANCE_LOW
//            ).apply {
//                description = "Channel for music playback controls"
//                 setSound(null, null)
//            }
//            val notificationManager: NotificationManager =
//                getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//    }
//
//    @OptIn(UnstableApi::class)
//    private fun showNotification() {
//
//        // Determine the correct icon for the play/pause action based on the playback state
//        val playPauseIcon = when (player.playbackState) {
//            Player.STATE_READY -> if (player.isPlaying) R.drawable.pause_48px else R.drawable.play_arrow_48px
//            Player.STATE_BUFFERING -> R.drawable.pause_48px // Custom icon for buffering state
//            Player.STATE_ENDED -> R.drawable.play_arrow_24px // Custom icon for ended state
//            Player.STATE_IDLE -> R.drawable.play_arrow_48px // Default to play icon for idle state
//            else -> R.drawable.play_arrow_48px // Fallback to play icon for any other state
//        }
//
//        // Create the Play/Pause action
//        val playPauseAction = NotificationCompat.Action.Builder(
//            playPauseIcon,
//            "Play/Pause",
//            PendingIntent.getService(
//                this,
//                0,
//                Intent(this, MusicService::class.java).apply { action = ACTION_PLAY_PAUSE },
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        ).build()
//
//        // Create the Stop action
//        val stopIntent = Intent(this, MusicService::class.java).apply {
//            action = ACTION_STOP
//        }
//        val stopPendingIntent = PendingIntent.getService(
//            this,
//            1,
//            stopIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Build and display the notification
//        val notification = NotificationCompat.Builder(this, "music_channel_id")
//            .setContentTitle("Playing Music")
//            .setSmallIcon(R.mipmap.ic_launcher) // Set the small icon
//            .addAction(playPauseAction) // Add the Play/Pause action
//            .setColor(ContextCompat.getColor(this, R.color.white)) // Set notification color
//            .setStyle(
//                androidx.media.app.NotificationCompat.MediaStyle()
//                    .setMediaSession(mediaSession.sessionToken)
//                    .setShowActionsInCompactView(0, 1, 2) // Show the Play/Pause action in compact view
//            )
//            .setPriority(NotificationCompat.PRIORITY_LOW) // Set notification priority
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    this,
//                    0,
//                    Intent(this, MusicService::class.java),
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//            ) // Set the main content intent
//            .setDeleteIntent(stopPendingIntent) // Set the Stop intent for notification dismissal
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Make the notification public
//            .build()
//
//        // Start foreground service with the notification
//        startForeground(1, notification)
//    }
//
//    private fun updateNotification(isPlaying: Boolean) {
//            showNotification()
//
//    }
//
//    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//
//        Log.i("TAG", "onStartCommand: ${intent?.action} and playerstate: ${player.playbackState}")
//
//        MediaButtonReceiver.handleIntent(mediaSession, intent)
//        intent?.let {
//            when (it.action) {
//                ACTION_PLAY -> {
//                    updateNotification(true)
//                    val uri = it.getStringExtra("MUSIC_URI")
//                    uri?.let {
//                        musicRepository.currentSong.value = uri
//                        playMusic(it)
//
//                    }
//                    musicViewModel.updateIsPlaying(true)
//                }
//                ACTION_PAUSE -> {
//
//                    //player.pause()
//                    mediaSession.controller.transportControls.pause()
//                    musicViewModel.updateIsPlaying(false)
//                    musicRepository.updatePlaybackPosition(player.currentPosition)
//                    updateNotification(false)
//                }
//                ACTION_PLAY_PAUSE -> {
//                    if (player.isPlaying) {
//                       // updateNotification(true)
//                        mediaSession.controller.transportControls.pause()
//                       // player.pause()
//                        musicViewModel.updateIsPlaying(false)
//                        musicRepository.updatePlaybackPosition(player.currentPosition)
//
//                    } else {
//                        //updateNotification(false)
//                        mediaSession.controller.transportControls.play()
//                        musicRepository.playbackPosition.value?.let { it1 ->
//                            mediaSession.controller.transportControls.seekTo(
//                                it1)
//                            musicViewModel.updateIsPlaying(true)
//                        }
//
//
//                    }
//                    updateNotification(true)
//                }
//                ACTION_STOP ->{
//                    updateNotification(false)
//                    stopSelf()
//                    player.pause()
//                    player.stop()
//                    player.release()
//                    musicViewModel.updateIsPlaying(false)
//                    mediaSession.controller.transportControls.pause()
//                    mediaSession.release()
//
//                    super.onDestroy()
//                }
//                else -> Unit
//            }
//        }
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//
//        stopSelf()
//        player.release()
//        mediaSession.release() // Release MediaSession when the service is destroyed
//        super.onDestroy()
//    }
//
//    companion object {
//        const val ACTION_PLAY = "ACTION_PLAY"
//        const val ACTION_PAUSE = "ACTION_PAUSE"
//        const val EXTRA_MUSIC_URI = "MUSIC_URI"
//        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
//        const val ACTION_STOP = "ACTION_STOP"
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        super.onBind(intent)
//        return null
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent?) {
//        stopForeground(STOP_FOREGROUND_REMOVE)
//        stopSelf()
//        super.onTaskRemoved(rootIntent)
//    }

}
