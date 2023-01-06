package com.zelgius.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.zelgius.cropkeeper.database.BuildConfig
import com.zelgius.database.dao.*
import com.zelgius.database.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(
    entities = [Period::class, PeriodHistory::class, Phase::class, Seed::class, Vegetable::class],
    version = BuildConfig.DATABASE_VERSION,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seedDao(): SeedDao
    abstract fun vegetableDao(): VegetableDao
    abstract fun phaseDao(): PhaseDao
    abstract fun periodDao(): PeriodDao
    abstract fun periodHistoryDao(): PeriodHistoryDao
    abstract fun fullVegetableDao(): FullVegetableDao
    abstract fun fullSeedDao(): FullSeedDao
}


class Converters {
    @TypeConverter
    fun fromNullableString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToNullableString(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}