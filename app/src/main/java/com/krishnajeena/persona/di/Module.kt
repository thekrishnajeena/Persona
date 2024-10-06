package com.krishnajeena.persona.di

import android.app.Application
import androidx.room.Room
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import com.krishnajeena.persona.data_layer.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun providerNoteDatabase(application : Application): NoteDatabase{
        return Room.databaseBuilder(
            context = application,
            NoteDatabase::class.java,
            "note_database.db")
        .build()
    }

    @Provides
    @Singleton
    fun providerBlogUrlDatabase(application: Application): BlogUrlDatabase {
        return Room.databaseBuilder(
            context = application,
            BlogUrlDatabase::class.java,
            "blog_url_database.db"
        ).build()
    }


}