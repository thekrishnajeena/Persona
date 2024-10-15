package com.krishnajeena.persona.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.krishnajeena.persona.ui_layer.MusicUrlViewModel
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MusicScreen(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(navController, "musicList"){

        composable("musicList"){

            MusicListScreen(navController = navController)

        }

        composable("musicPlayer"){

            MusicPlayer()

        }

    }
}



@RequiresApi(Build.VERSION_CODES.O)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicListScreen(modifier: Modifier = Modifier, navController: NavController) {

    val context = LocalContext.current
    val musicViewModel = MusicUrlViewModel(context)

val musicModel : MusicViewModel = hiltViewModel()

    val musicList by musicViewModel.musicList.observeAsState(emptyList())

    val isPlaying by musicModel.isPlaying.observeAsState(false)
    val currentSong by musicModel.currentSong.observeAsState("Persona Music")
    val currentSongUri by musicModel.currentSongUri.observeAsState()


    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = GetCustomContents(isMultiple = true),
        onResult = {
            musics ->
            musics.forEach{ music ->
                musicViewModel.addMusic(music, context)

            }
        }
    )

    Scaffold(modifier = Modifier
        .fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
            onClick = {
               // showBottomSheet = true
                musicPickerLauncher.launch(("audio/*"))
              //  musicList = musicViewModel.musicList.toList()
            },
            elevation = FloatingActionButtonDefaults.elevation(10.dp),
            modifier = Modifier.padding(10.dp)
        ){Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
        },
     ) { innerPadding ->

        if(musicList.isEmpty()){
            Image(painter = painterResource(R.drawable.woman_added_song_to_playlist),
                contentDescription = null, alignment = Alignment.Center)
        }
else {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(PaddingValues(bottom = innerPadding.calculateBottomPadding()))
            ) {

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(
                        items = musicList,
                        key = { _, music -> music.absolutePath }) { _, music ->
                        MusicItem(
                            name = music,
                            musicViewModel = musicViewModel,
                            musicModel = musicModel
                        )


                    }
                }
                // if (isPlaying) {
                BottomPlaybackController(currentSong,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    isPlaying,
                    onPlay = { musicModel.playMusic(context, currentSongUri!!, false) },
                    onPauseClick = { musicModel.pauseMusic(context) })
                // }
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
            .padding(8.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Now Playing: $currentSong", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.fillMaxWidth(.7f))
        IconButton(onClick = { if(isPlaying)onPauseClick()
        else onPlay()}, modifier = Modifier.fillMaxWidth(.3f)) {
            Icon(if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Pause", tint = Color.White)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicItem(modifier: Modifier = Modifier, name: File, musicViewModel: MusicUrlViewModel,
              musicModel : MusicViewModel= hiltViewModel()
) {

    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    musicViewModel.removeMusic(name)
                }
                else -> Unit
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {DismissBackground(dismissState)},
    ) {

        Card(
            onClick = {
                musicModel.updatePlaybackPosition(0L)

musicModel.playMusic(context, name.toUri(), true)
                      }, modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            elevation = CardDefaults.elevatedCardElevation(10.dp),
            shape = RoundedCornerShape(20)
        ) {

            Row(modifier = Modifier.fillMaxWidth()){

                Image(painter = painterResource(R.drawable.v790_nunny_37), contentDescription = null,
                    modifier = Modifier.weight(1f))
                Text(text = name.name, fontSize = 20.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(3f),
                    textAlign = TextAlign.Start,
                    maxLines = 2)
            }
        }

    }
}

@Composable
fun MusicPlayer(modifier: Modifier = Modifier) {
    
}