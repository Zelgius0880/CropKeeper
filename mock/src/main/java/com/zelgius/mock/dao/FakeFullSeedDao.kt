package com.zelgius.mock.dao

import com.zelgius.database.dao.FullSeedDao
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.Seed
import java.time.LocalDate

class FakeFullSeedDao : FullSeedDao {
    override suspend fun delete(vararg history: PeriodHistory) {

    }

    override suspend fun delete(vararg seed: Seed) {

    }

    override suspend fun insert(vararg history: PeriodHistory) {

    }

    override suspend fun update(seed: Seed) {

    }

    override suspend fun updateHistoryEndDate(date: LocalDate, seedUid: String, periodUid: String) {

    }

    override suspend fun deleteHistory(seedUid: String) {
    }

}