package com.krishnajeena.persona.reelstack

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [VideoUri::class], version = 1, exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {
    abstract fun videoUriDao(): VideoUriDao
}
