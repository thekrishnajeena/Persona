package com.krishnajeena.persona.reelstack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface VideoUriDao {
    @Query("SELECT * FROM video_uris")
    fun getAllVideos(): Flow<List<VideoUri>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(videoUri: VideoUri)

    @Delete
    suspend fun deleteVideo(videoUri: VideoUri)
}
