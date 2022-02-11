package com.zelgius.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.zelgius.database.model.*

@Dao
interface PeriodHistoryDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg history: PeriodHistory)

    @Update
    suspend fun update(vararg history: PeriodHistory)

    @Delete
    suspend fun delete(vararg history: PeriodHistory)

    @Query("SELECT * FROM period_history")
    suspend fun getAll(): List<PeriodHistory>

    @Query(
        "SELECT period_history.* FROM period_history " +
                "WHERE period_history.period_uid = :periodUid AND period_history.start_date LIKE :year||'-%' AND end_date IS NOT NULL"
    )
    suspend fun getPeriodWithHistoryForYear(
        periodUid: String,
        year: Int
    ): List<PeriodHistory>

    @Query(
        "SELECT DISTINCT strftime('%Y',h.start_date) FROM period_history h " +
                "JOIN period p ON p.period_uid = h.period_uid " +
                "JOIN vegetable v ON v.vegetable_uid = p.vegetable_uid " +
                "WHERE v.vegetable_uid = :vegetableUid"
    )
    suspend fun getHistoryYears(vegetableUid: String): List<Int>
}