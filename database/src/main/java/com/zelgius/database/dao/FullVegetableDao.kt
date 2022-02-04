package com.zelgius.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.zelgius.database.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FullVegetableDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg vegetables: Vegetable)


    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg phases: Phase)

    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg periods: Period)

    @Query("DELETE FROM period WHERE period_uid NOT IN (:periodUids) AND vegetable_uid = :vegetableUid")
    suspend fun deletePeriodsNotIn(periodUids: List<String>, vegetableUid: String)

    @Query("UPDATE period SET is_deleted = 1 WHERE vegetable_uid  IN (:vegetableUid)")
    suspend fun softDeletePeriod(vararg vegetableUid: String)

    @Query("DELETE FROM period WHERE vegetable_uid IN (:vegetableUid)")
    suspend fun deletePeriod(vararg vegetableUid: String)

    @Query("UPDATE vegetable SET is_deleted = 1 WHERE vegetable_uid IN (:vegetableUid)")
    suspend fun softDeleteVegetable(vararg vegetableUid: String)

    @Query("DELETE FROM vegetable WHERE vegetable_uid IN (:vegetableUid)")
    suspend fun deleteVegetable(vararg vegetableUid: String)

    @Transaction
    suspend fun insert(vegetable: Vegetable, periods: List<PeriodWithPhase>) {
        insert(vegetable)

        periods.forEachIndexed { index, p ->
            insert(p.phase)
            insert(
                p.period.copy(
                    order = index,
                    vegetableUid = vegetable.vegetableUid,
                    phaseUid = p.period.phaseUid
                )
            )
        }

        deletePeriodsNotIn(periods.map { it.period.periodUid }, vegetable.vegetableUid)
    }

    @Transaction
    suspend fun delete(vegetable: Vegetable) {
        deletePeriod(vegetable.vegetableUid)
        deleteVegetable(vegetable.vegetableUid)
    }

    @Transaction
    suspend fun softDelete(vegetable: Vegetable) {
        softDeletePeriod(vegetable.vegetableUid)
        softDeleteVegetable(vegetable.vegetableUid)
    }
}