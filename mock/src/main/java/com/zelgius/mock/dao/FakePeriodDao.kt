package com.zelgius.mock.dao

import com.zelgius.database.dao.PeriodDao
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.mock.periodSample
import com.zelgius.mock.periodSampleWithPhaseAndHistorySample

class FakePeriodDao : PeriodDao {
    override suspend fun insert(vararg periods: Period) {}

    override suspend fun update(vararg periods: Period) {}

    override suspend fun delete(vararg periods: Period) {}

    override suspend fun getAll(): List<Period> = periodSample.map { it.period }
    override suspend fun get(uid: String): Period? =
        periodSample.map { it.period }.find { it.periodUid == uid }

    override suspend fun getPeriodWithHistory(periodUids: List<String>): List<PeriodWithPhaseAndHistory> =
        periodSampleWithPhaseAndHistorySample.filter {
            it.period.periodUid in periodUids
        }

    override fun getHistoryYears(vegetableUid: String): List<Int> =
        periodSampleWithPhaseAndHistorySample
            .flatMap { it.periodHistories }
            .map { it.startDate.year }
            .distinct()

    override suspend fun getPeriodsForVegetable(vegetableUid: String): List<Period> =
        periodSample.filter { it.period.vegetableUid == vegetableUid }.map { it.period }

}