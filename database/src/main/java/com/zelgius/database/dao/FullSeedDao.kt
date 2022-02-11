package com.zelgius.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.Seed
import java.time.LocalDate

@Dao
interface FullSeedDao {
    @Delete
    suspend fun delete(vararg history: PeriodHistory)

    @Delete
    suspend fun delete(vararg seed: Seed)

    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg history: PeriodHistory)

    @Update
    suspend fun update(seed: Seed)

    @Query("UPDATE period_history SET end_date = :date WHERE seed_uid = :seedUid AND period_uid = :periodUid")
    suspend fun updateHistoryEndDate(date: LocalDate, seedUid: String, periodUid: String)

    @Query("DELETE FROM period_history WHERE seed_uid = :seedUid")
    suspend fun deleteHistory(seedUid: String)

    @Transaction
    suspend fun newHistory(
        seed: Seed,
        previousPeriod: Period,
        period: Period,
        periodsToDelete: List<Period>
    ) {
        if (periodsToDelete.isNotEmpty()) {
            delete(*periodsToDelete.map {
                PeriodHistory(
                    seedUid = seed.seedUid,
                    periodUid = it.periodUid
                )
            }.toTypedArray())
        }

        updateHistoryEndDate(LocalDate.now(), seed.seedUid, previousPeriod.periodUid)
        insert(PeriodHistory(seedUid = seed.seedUid, periodUid = period.periodUid))
        update(seed.copy(actualPeriodUid = period.periodUid))
    }

    @Transaction
    suspend fun close(item: FullSeed) {
        updateHistoryEndDate(LocalDate.now(), item.seed.seedUid, item.actualPeriod.period.periodUid)
        update(item.seed.copy(isClosed = true))
    }

    @Transaction
    suspend fun hardDelete(seed: Seed) {
        deleteHistory(seed.seedUid)
        delete(seed)
    }
}