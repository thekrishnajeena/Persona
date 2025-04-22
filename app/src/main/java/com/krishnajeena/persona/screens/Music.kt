package com.krishnajeena.persona.screens

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.krishnajeena.persona.GetCustomContents
import com.krishnajeena.persona.R
import com.krishnajeena.persona.components.HomeEvent
import com.krishnajeena.persona.model.HomeViewModel
import com.krishnajeena.persona.ui_states.MediaControllerUiState
import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.ui_states.PlayerState
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.data_layer.Song


@OptIn(UnstableApi::class)
@Composable
fun MusicScreen(sharedViewModel: SharedViewModel) {

    val musicViewModel: MusicViewModel = hiltViewModel()
    val musicList by musicViewModel.musicList.observeAsState(emptyList())
    val isPlaying by musicViewModel.isPlaying.observeAsState(false)

    val mainViewModel: HomeViewModel = hiltViewModel()

    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { selectedMusics ->
            selectedMusics.forEach { music ->
                musicViewModel.addMusic(music)

            }
            mainViewModel.homeUiState.copy(songs = musicViewModel.musicList.value?.map { Song(it.name, it.toUri().toString()) })
            mainViewModel.onEvent(
                HomeEvent.SetSongs(musicViewModel.musicList.value?.map { Song(it.name, it.toUri().toString()) } ?: emptyList())
            )

        }
    )

    val musicControllerUiState = sharedViewModel.musicControllerUiState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { musicPickerLauncher.launch("audio/*") },
                elevation = FloatingActionButtonDefaults.elevation(10.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { _ ->
        if (musicList.isEmpty()) {
            EmptyStateScreen()
        } else {

            MusicListContent(
                musicViewModel = musicViewModel,
                musicControllerUiState = musicControllerUiState,
                onEvent = mainViewModel::onEvent,
                mainViewModel = mainViewModel
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
    musicViewModel: MusicViewModel,
    musicControllerUiState: MediaControllerUiState,
    onEvent: (HomeEvent) -> Unit,
    mainViewModel: HomeViewModel
) {

    val isInitialized = rememberSaveable { mutableStateOf(false) }

    val musicList by musicViewModel.musicList.observeAsState(emptyList())

    if (!isInitialized.value) {
        LaunchedEffect(key1 = Unit, key2 = musicViewModel.musicList.observeAsState()) {
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
            item(0){
                Card(modifier = Modifier.align(Alignment.TopCenter), shape = RoundedCornerShape(bottomEndPercent = 20, bottomStartPercent = 20)
                    , elevation = CardDefaults.elevatedCardElevation(10.dp)){
                BottomPlaybackController(
                    onEvent = mainViewModel::onEvent,
                    song = musicControllerUiState.currentSong,
                    playerState = musicControllerUiState.playerState,
                    )
                }
            }
            itemsIndexed(
                items = musicList.map { Song(it.name, it.toUri().toString()) }, //musicList,
                key = { _, music -> music.songUrl }
            ) { _, music ->
                MusicItem(
                    fileUri = music.songUrl.toUri(),
                    musicViewModel = musicViewModel,
                    onClick = {
                        onEvent(HomeEvent.OnSongSelected(music))
                        onEvent(HomeEvent.PlaySong)

                    },
                    mainViewModel = mainViewModel
                )
            }
        }



    }
}

@Composable
fun MusicItem(
    fileUri: Uri,
    musicViewModel: MusicViewModel,
    onClick: () -> Unit,
    mainViewModel: HomeViewModel
) {


    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                musicViewModel.removeMusic(fileUri.toFile())
                mainViewModel.homeUiState.copy(songs = musicViewModel.musicList.value?.map { Song(it.name, it.toUri().toString()) })
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
                onClick()
               },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(20),
            elevation = CardDefaults.elevatedCardElevation(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.v790_nunny_37),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp) // Set fixed image size
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp)) // spacing between image and text
    Text(
                        text = fileUri.toFile().name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

            }

        }
    }
}

@Composable
fun BottomPlaybackController(
    modifier: Modifier = Modifier,
    onEvent: (HomeEvent) -> Unit,
    song: Song?,
    playerState: PlayerState?
) {

       Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the current song title
            Text(
                text = song?.title ?: "Select a music to play!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

           val painter =
               if (playerState == PlayerState.PLAYING)
                   Icons.Default.Pause else Icons.Default.PlayArrow

            // Play/Pause button
            IconButton(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth(0.3f)
            ) {
                // Icon updates based on the playback state
                Icon(
                    imageVector = painter,
                    contentDescription = "Music",
                    modifier = Modifier.clickable
                         {
                        if (playerState == PlayerState.PLAYING) {
                            onEvent(HomeEvent.PauseSong)
                        } else {
                            onEvent(HomeEvent.ResumeSong)
                        }
                    },
                    tint = Color.White
                )
            }


    }
}



