package com.example.joymp3.di

import com.example.joymp3.repository.SongRepository
import com.example.joymp3.ui.fragments.song.SongViewModelFactory
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
    fun provideSongRepository(): SongRepository {
        return SongRepository()
    }

    @Provides
    fun provideViewModelFactory(repository: SongRepository): SongViewModelFactory {
        return SongViewModelFactory(repository)
    }
}