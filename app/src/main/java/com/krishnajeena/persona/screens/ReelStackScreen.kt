package com.krishnajeena.persona.screens

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.krishnajeena.persona.reelstack.VideoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReelScreen(modifier: Modifier = Modifier) {

    val viewModel: VideoViewModel = hiltViewModel()

    VideoPagerScreen(viewModel)
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPagerScreen(viewModel: VideoViewModel) {
    val videoUris by viewModel.videoUris.collectAsState()
    val pagerState = rememberPagerState(pageCount = {  if(videoUris.size>1)Int.MAX_VALUE else if(videoUris.size==1) 1 else 0 },
        initialPage = Int.MAX_VALUE/2)
    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(pagerState.currentPage) }


    // Register ActivityResultLauncher to pick videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            uris.forEach { uri ->
                val contentResolver: ContentResolver = context.contentResolver
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.addVideoUri(uri.toString()) // Add to Room DB
            }
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { videoPickerLauncher.launch("video/*") },

            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Video")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (videoUris.isEmpty()) {
                Text("No videos available", modifier = Modifier.align(Alignment.Center))
            } else {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val index = page % videoUris.size // Looping logic
                    val videoUri = videoUris.getOrNull(index)

                    videoUri?.let {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.StartToEnd) {
                                    // Remove video and move to next one
                                    viewModel.removeVideo(videoUri)

                                    currentIndex = if (index < videoUris.lastIndex) index + 1 else 0

                                }
                                true
                            },
                            positionalThreshold = { it * 0.25f }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = false,
                            backgroundContent = { DismissBackground(dismissState) },
                        ) {
                            VideoPlayerScreen(
                            viewModel = viewModel,
                            videoUri = videoUris[page % videoUris.size].uri,
                            isVisible = pagerState.currentPage == page
                        )
                        }
                    }
                }
                LaunchedEffect(currentIndex) {
                    pagerState.animateScrollToPage(currentIndex)
                }
            }}

    }
}

@Composable
fun VideoPlayerScreen(viewModel: VideoViewModel, videoUri: String, isVisible: Boolean) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() } // Single ExoPlayer instance
    val isPlaying = remember { mutableStateOf(false) }
    val showControls = remember { mutableStateOf(false) }
    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(videoUri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.prepare()
    }

    // Pause/Play based on visibility
    LaunchedEffect(isVisible) {
        if (isVisible) {
            isPlaying.value = true
            exoPlayer.playWhenReady = true
        } else {
            isPlaying.value = false
            exoPlayer.playWhenReady = false
            exoPlayer.seekTo(0)  // Reset position to avoid overlap
        }
    }



    // Release ExoPlayer when the composable is removed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Hide play/pause button after 3s
    fun startHideControlsTimer() {
        coroutineScope.launch {
            delay(3000)
            showControls.value = false
        }
    }

    LaunchedEffect(isPlaying.value) {
        if (isPlaying.value) {
            showControls.value = false
            startHideControlsTimer()
        }
    }

    DisposableEffect(isPlaying.value) {
        if (isPlaying.value) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                isPlaying.value = !isPlaying.value
                exoPlayer.playWhenReady = isPlaying.value
                showControls.value = true
                if (isPlaying.value) startHideControlsTimer()
            }
        , contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    keepScreenOn = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showControls.value || !isPlaying.value,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            IconButton(
                onClick = {
                    isPlaying.value = !isPlaying.value
                    exoPlayer.playWhenReady = isPlaying.value
                    showControls.value = true
                    if (isPlaying.value) startHideControlsTimer()
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}
