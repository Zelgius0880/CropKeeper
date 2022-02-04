package com.zelgius.mock.dao

import com.zelgius.database.dao.PeriodHistoryDao
import com.zelgius.database.model.*
import com.zelgius.mock.periodSampleWithPhaseAndHistorySample

class FakePeriodHistoryDao : PeriodHistoryDao {
    override suspend fun insert(vararg history: PeriodHistory) {}

    override suspend fun update(vararg history: PeriodHistory) {}

    override suspend fun delete(vararg history: PeriodHistory) {}

    override suspend fun getAll(): List<PeriodHistory> =
        periodSampleWithPhaseAndHistorySample.flatMap { it.periodHistories }

    override suspend fun getPeriodWithHistoryForYear(
        periodUid: String,
        year: Int
    ): List<PeriodHistory> = periodSampleWithPhaseAndHistorySample.filter {
        it.period.periodUid == periodUid
    }.flatMap { p ->
        p.periodHistories.filter { it.startDate.year == year }
    }


    override suspend fun getHistoryYears(vegetableUid: String): List<Int> =
        periodSampleWithPhaseAndHistorySample
            .flatMap { it.periodHistories }
            .map { it.startDate.year }
            .distinct()

}
