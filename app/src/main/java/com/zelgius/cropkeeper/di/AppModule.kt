package com.zelgius.cropkeeper.di

import android.content.Context
import androidx.room.Room
import com.zelgius.database.AppDatabase
import com.zelgius.database.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "crop_keeper_db"
    )
        .createFromAsset("initial_db.db")
        .fallbackToDestructiveMigration()
        .build()
}