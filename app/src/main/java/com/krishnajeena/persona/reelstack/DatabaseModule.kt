package com.krishnajeena.persona.reelstack

import android.content.Context
import androidx.room.Room
import com.krishnajeena.persona.reelstack.VideoDatabase
import com.krishnajeena.persona.reelstack.VideoUriDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VideoDatabase {
        return Room.databaseBuilder(
            context,
            VideoDatabase::class.java,
            "video_database"
        ).build()
    }

    @Provides
    fun provideVideoUriDao(database: VideoDatabase): VideoUriDao {
        return database.videoUriDao()
    }
}
