package com.krishnajeena.persona.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.GetCustomContents
import com.krishnajeena.persona.R
import com.krishnajeena.persona.components.HomeEvent
import com.krishnajeena.persona.model.HomeViewModel
import com.krishnajeena.persona.model.MediaControllerUiState
import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.services.MusicService
//import com.krishnajeena.persona.services.MusicService.Companion.ACTION_PAUSE
//import com.krishnajeena.persona.services.MusicService.Companion.ACTION_PLAY
//import com.krishnajeena.persona.services.MusicService.Companion.ACTION_PLAY_PAUSE
import java.io.File


@Composable
fun MusicScreen(modifier: Modifier = Modifier, sharedViewModel: SharedViewModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "musicList") {
        composable("musicList") { MusicListScreen(navController, sharedViewModel) }
        composable("musicPlayer") { MusicPlayerScreen() }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun MusicListScreen(navController: NavController, sharedViewModel: SharedViewModel) {
    val context = LocalContext.current
//    val musicViewModel: MusicUrlViewModel = hiltViewModel()
    val musicViewModel: MusicViewModel = hiltViewModel()
Log.i("TAG", musicViewModel.toString())
    val musicList by musicViewModel.musicList.observeAsState(emptyList())
    val isPlaying by musicViewModel.isPlaying.observeAsState(false)
    val currentSong by musicViewModel.currentSong.observeAsState("No Song Playing")
    val currentSongUri by musicViewModel.currentSongUri.observeAsState()

    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { selectedMusics ->
            selectedMusics.forEach { music ->
                musicViewModel.addMusic(music)
            }
        }
    )

    val musicControllerUiState = sharedViewModel.musicControllerUiState
    val activity = (LocalContext.current as ComponentActivity)


    LaunchedEffect(isPlaying) { Log.i("TAG", isPlaying.toString()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { musicPickerLauncher.launch("audio/*") },
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        if (musicList.isEmpty()) {
            EmptyStateScreen()
        } else {



            MusicListContent(
                musicList = musicList,
                musicViewModel = musicViewModel,
                context = context,
                isPlaying = isPlaying,
                currentSong = currentSong,
                currentSongUri = currentSongUri,
                innerPadding = innerPadding,
                musicControllerUiState = musicControllerUiState
            )
        }
    }
}

@Composable
fun EmptyStateScreen() {
    Image(
        painter = painterResource(R.drawable.undraw_media_player_re_rdd2),
        contentDescription = null,
        alignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun MusicListContent(
    musicList: List<File>,
    musicViewModel: MusicViewModel,
    context: Context,
    isPlaying: Boolean,
    currentSong: String,
    currentSongUri: Uri?,
    innerPadding: PaddingValues,
    musicControllerUiState: MediaControllerUiState
) {

    val mainViewModel: HomeViewModel = hiltViewModel()
    val isInitialized = rememberSaveable { mutableStateOf(false) }

    if (!isInitialized.value) {
        LaunchedEffect(key1 = Unit) {
            mainViewModel.onEvent(HomeEvent.FetchSong)
            isInitialized.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(
                items = mainViewModel.homeUiState.songs.orEmpty(), //musicList,
                key = { _, music -> music.songUrl }
            ) { _, music ->
                MusicItem(
                    file = music.songUrl.toUri().toFile(),
                    musicViewModel = musicViewModel
                )
            }
        }



        musicViewModel.isPlaying.value?.let {
            BottomPlaybackController(
                currentSong = currentSong,
                modifier = Modifier.align(Alignment.BottomCenter),
                onPauseClick = { musicViewModel.pauseMusic()

                               val intent = Intent(context, MusicService::class.java)
                    intent.action = ACTION_PAUSE
                    startForegroundService(context, intent)
                               },
                onPlay = {
                    if (currentSongUri != null) {
                        musicViewModel.playMusic(currentSongUri, true)

                        val intent = Intent(context, MusicService::class.java)
                        intent.action = ACTION_PLAY_PAUSE
                        startForegroundService(context, intent)
                    }
                    else musicViewModel.playMusic(musicList[0].toUri(), true)
                },
                musicViewModel = musicViewModel
            )
        }

    }
}

@OptIn(UnstableApi::class)
@Composable
fun MusicItem(
    file: File,
    musicViewModel: MusicViewModel
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                musicViewModel.removeMusic(file)
            }
            true
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = { DismissBackground(dismissState) }
    ) {
        Card(
            onClick = {
                //musicViewModel.updatePlaybackPosition(0L)
                Log.i("TAG", "MusicItem: ${file.toUri()}")
                musicViewModel.playMusic(file.toUri(), true)
               },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(20),
            elevation = CardDefaults.elevatedCardElevation(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.v790_nunny_37),
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = file.name,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(3f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun BottomPlaybackController(
    musicViewModel: MusicViewModel,
    currentSong: String,
    modifier: Modifier = Modifier,
    onPauseClick: () -> Unit = {},
    onPlay: () -> Unit
) {
    val context = LocalContext.current

    // Observe the playback state and music list from the ViewModel
    val isPlay by musicViewModel.isPlaying.observeAsState(initial = false)
    val musicList by musicViewModel.musicList.observeAsState(initial = emptyList())

    LaunchedEffect(isPlay) {
        Log.i("TAG", "updateIsPlaying inside music: ${isPlay}")
    }

    val playPauseIcon by remember(isPlay) {
        derivedStateOf { if (isPlay) Icons.Default.Pause else Icons.Default.PlayArrow }
    }

       Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the current song title
            Text(
                text = "Now Playing: $currentSong",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            // Play/Pause button
            IconButton(
                onClick = {
                    if (musicList.isNotEmpty()) {
                        // Send action to the music service to toggle play/pause
                        val intent = Intent(context, MusicService::class.java).apply {
                            action = ACTION_PLAY_PAUSE
                        }
                        context.startForegroundService(intent)

                        // Call respective callbacks based on the state
                        if (isPlay) onPauseClick() else onPlay()
                    } else {
                        // Show a toast if the music list is empty
                        Toast.makeText(context, "There is no music to play", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                // Icon updates based on the playback state
                Icon(
                    imageVector = playPauseIcon,
                    contentDescription = if (isPlay) "Pause" else "Play",
                    tint = Color.White
                )
            }


    }
}



@Composable
fun MusicPlayerScreen() {
    // Placeholder for the Music Player Screen implementation
}
