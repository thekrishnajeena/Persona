package com.krishnajeena.persona.data_layer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    var title: String,
    var discription : String,
    var dateDated: Long
)
