package com.krishnajeena.persona.data_layer

import androidx.compose.ui.geometry.Offset
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var discription: String,
    var dateDated: Long,

    @Embedded(prefix = "position_")
    var position: OffsetEntity = OffsetEntity((100..400).random().toFloat(), (100..400).random().toFloat()),

    @Embedded(prefix = "size_")
    var size: OffsetEntity = OffsetEntity(200f, 120f) // Default note size
)

data class OffsetEntity(
    var x: Float = 0f,
    var y: Float = 0f
) {
    fun toOffset() = Offset(x, y)

    companion object {
        fun fromOffset(offset: Offset) = OffsetEntity(offset.x, offset.y)
    }
}

