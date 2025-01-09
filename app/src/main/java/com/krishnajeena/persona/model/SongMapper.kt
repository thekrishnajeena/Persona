package com.krishnajeena.persona.model

import androidx.media3.common.MediaItem

fun MediaItem.toSong() =

    Song(
        title = mediaMetadata.title.toString(),
        songUrl = mediaId,
        )