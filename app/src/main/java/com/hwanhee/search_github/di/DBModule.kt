package com.hwanhee.search_github.di

import android.content.Context
import androidx.room.Room
import com.hwanhee.search_github.db.SearchWordDao
import com.hwanhee.search_github.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DBModule {
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room
            .databaseBuilder(context, AppDatabase::class.java, "search_app.db")
            .build()

    @Singleton
    @Provides
    fun provideSearchWordDao(appDatabase: AppDatabase): SearchWordDao = appDatabase.searchWordDao()
}