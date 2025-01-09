package com.krishnajeena.persona.model

import androidx.media3.common.MediaItem
import com.krishnajeena.persona.data_layer.Song

fun MediaItem.toSong() =

    Song(
        title = mediaMetadata.title.toString(),
        songUrl = mediaId,
        )