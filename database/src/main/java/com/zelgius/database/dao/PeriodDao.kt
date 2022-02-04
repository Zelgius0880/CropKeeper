package com.zelgius.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.zelgius.database.model.*

@Dao
interface PeriodDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg periods: Period)

    @Update
    suspend fun update(vararg periods: Period)

    @Delete
    suspend fun delete(vararg periods: Period)

    @Query("SELECT * FROM period  WHERE is_deleted = 0")
    suspend fun getAll(): List<Period>

    @Query("SELECT * FROM period WHERE period_uid = :uid")
    suspend fun get(uid: String): Period?

    @Query("SELECT * FROM period WHERE period_uid IN (:periodUids) AND is_deleted = 0 ORDER BY `order`")
    @Transaction
    suspend fun getPeriodWithHistory(
        periodUids: List<String>
    ): List<PeriodWithPhaseAndHistory>

    @Query(
        "SELECT strftime('%Y',h.start_date) FROM period_history h " +
                "JOIN period p ON p.period_uid = h.period_uid " +
                "JOIN vegetable v ON v.vegetable_uid = p.vegetable_uid " +
                "WHERE v.vegetable_uid = :vegetableUid  AND p.is_deleted = 0 " +
                "ORDER BY p.`order`"
    )
    fun getHistoryYears(vegetableUid: String): List<Int>


    @Query("SELECT period.* FROM period JOIN phase ON phase.phase_uid = period.phase_uid WHERE vegetable_uid = :vegetableUid  AND period.is_deleted = 0 ORDER BY `order`")
    suspend fun getPeriodsForVegetable(
        vegetableUid: String
    ): List<Period>

}