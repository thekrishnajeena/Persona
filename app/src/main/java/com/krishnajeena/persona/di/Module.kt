package com.krishnajeena.persona.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import com.krishnajeena.persona.data_layer.NoteDatabase
import com.krishnajeena.persona.model.MusicViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return Room.databaseBuilder(
            application,
            NoteDatabase::class.java,
            "note_database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBlogUrlDatabase(application: Application): BlogUrlDatabase {
        return Room.databaseBuilder(
            application,
            BlogUrlDatabase::class.java,
            "blog_url_database.db"
        ).build()
    }
}
