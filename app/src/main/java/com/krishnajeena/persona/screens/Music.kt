package com.krishnajeena.persona.screens

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krishnajeena.persona.GetCustomContents
import com.krishnajeena.persona.R
import com.krishnajeena.persona.model.MusicViewModel
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MusicScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "musicList") {
        composable("musicList") { MusicListScreen(navController) }
        composable("musicPlayer") { MusicPlayerScreen() }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicListScreen(navController: NavController) {
    val context = LocalContext.current
//    val musicViewModel: MusicUrlViewModel = hiltViewModel()
    val musicViewModel: MusicViewModel = hiltViewModel()

    val musicList by musicViewModel.musicList.observeAsState(emptyList())
    val isPlaying by musicViewModel.isPlaying.observeAsState(false)
    val currentSong by musicViewModel.currentSong.observeAsState("No Song Playing")
    val currentSongUri by musicViewModel.currentSongUri.observeAsState()

    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = { selectedMusics ->
            selectedMusics.forEach { music ->
                musicViewModel.addMusic(music, context)
            }
        }
    )

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
                innerPadding = innerPadding
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
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            itemsIndexed(
                items = musicList,
                key = { _, music -> music.absolutePath }
            ) { _, music ->
                MusicItem(
                    file = music,
                    musicViewModel = musicViewModel
                )
            }
        }

        BottomPlaybackController(
            currentSong = currentSong,
            modifier = Modifier.align(Alignment.BottomCenter),
            isPlaying = isPlaying,
            onPlay = { musicViewModel.playMusic(currentSongUri!!, context) },
            onPauseClick = { musicViewModel.pauseMusic(context) }
        )
    }
}

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
                musicViewModel.updatePlaybackPosition(0L)
                musicViewModel.playMusic(file.toUri(), context)
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

@Composable
fun BottomPlaybackController(
    currentSong: String,
    modifier: Modifier,
    isPlaying: Boolean,
    onPauseClick: () -> Unit = {},
    onPlay: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Now Playing: $currentSong",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        IconButton(
            onClick = { if (isPlaying) onPauseClick() else onPlay() },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}


@Composable
fun MusicPlayerScreen() {
    // Placeholder for the Music Player Screen implementation
}
