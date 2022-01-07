package com.zelgius.database.dao

import androidx.room.*
import com.zelgius.database.model.*

@Dao
interface PeriodDao {
    @Insert
    suspend fun insert(vararg periods: Period)

    @Update
    suspend fun update(vararg periods: Period)

    @Delete
    suspend fun delete(vararg periods: Period)

    @Query("SELECT * FROM period")
    suspend fun getAll(): List<Period>

    @Query("SELECT * FROM period WHERE period_uid = :uid")
    suspend fun get(uid: String): Period?

    @Query("SELECT * FROM period WHERE period_uid IN (:periodUids)")
    @Transaction
    suspend fun getPeriodWithHistory(
        periodUids: List<String>
    ): List<PeriodWithPhaseAndHistory>

    @Query(
        "SELECT strftime('%Y',h.start_date) FROM period_history h " +
                "JOIN period p ON p.period_uid = h.period_uid " +
                "JOIN vegetable v ON v.vegetable_uid = p.vegetable_uid " +
                "WHERE v.vegetable_uid = :vegetableUid"
    )
    fun getHistoryYears(vegetableUid: String): List<Int>


    @Query("SELECT phase.* FROM period JOIN phase ON phase.phase_uid = period.phase_uid WHERE vegetable_uid = :vegetableUid ORDER BY `order`")
    @Transaction
    suspend fun getPeriodWithPhaseForVegetable(
        vegetableUid: String
    ): List<PeriodWithPhase>

}