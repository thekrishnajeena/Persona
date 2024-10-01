package com.krishnajeena.persona.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blogUrls")
data class BlogUrl(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String
)
