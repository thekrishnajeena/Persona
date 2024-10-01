package com.krishnajeena.persona.data_layer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlogUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrl(url: BlogUrl)

    @Query("SELECT * from blogUrls")
    suspend fun getAllUrls(): List<BlogUrl>

    @Delete
    suspend fun deleteUrl(url : BlogUrl)
}