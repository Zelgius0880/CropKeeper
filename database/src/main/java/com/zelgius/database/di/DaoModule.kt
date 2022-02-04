package com.zelgius.database.di

import com.zelgius.database.AppDatabase
import com.zelgius.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {
    @Singleton
    @Provides
    fun provideSeedDao(db: AppDatabase): SeedDao = db.seedDao()

    @Singleton
    @Provides
    fun provideVegetableDao(db: AppDatabase): VegetableDao = db.vegetableDao()

    @Singleton
    @Provides
    fun providePeriodDao(db: AppDatabase): PeriodDao = db.periodDao()

    @Singleton
    @Provides
    fun providePeriodHistoryDao(db: AppDatabase): PeriodHistoryDao = db.periodHistoryDao()

    @Singleton
    @Provides
    fun providePhaseDao(db: AppDatabase): PhaseDao = db.phaseDao()

    @Singleton
    @Provides
    fun provideFullVegetableDao(db: AppDatabase): FullVegetableDao = db.fullVegetableDao()
}