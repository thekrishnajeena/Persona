package com.krishnajeena.persona.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.krishnajeena.persona.MusicDataSource
import com.krishnajeena.persona.components.MusicControllerImpl
import com.krishnajeena.persona.data_layer.BlogUrlDatabase
import com.krishnajeena.persona.data_layer.MusicRepository
import com.krishnajeena.persona.data_layer.MusicRepositoryImpl
import com.krishnajeena.persona.data_layer.NoteDatabase
import com.krishnajeena.persona.model.MusicViewModel
import com.krishnajeena.persona.other.QuoteRepository
import com.krishnajeena.persona.services.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

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

    @Singleton
    @Provides
    fun provideMusicRepository(
        musicDataSource: MusicDataSource
    ): MusicRepository = MusicRepositoryImpl(musicDataSource)

    @Singleton
    @Provides
    fun provideMusicDataSource(): MusicDataSource = MusicDataSource()


    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)

}
