package com.krishnajeena.persona.reelstack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_uris")
data class VideoUri(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String
)
