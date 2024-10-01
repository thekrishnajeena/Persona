package com.krishnajeena.persona.data_layer

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BlogUrl::class], version = 1, exportSchema = false)
abstract class BlogUrlDatabase : RoomDatabase() {
    abstract val blogUrlDao : BlogUrlDao
}