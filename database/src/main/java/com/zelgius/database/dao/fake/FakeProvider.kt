package com.zelgius.database.dao.fake

import com.zelgius.database.repository.PeriodHistoryRepository
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.SeedRepository
import com.zelgius.database.repository.VegetableRepository

object FakeProvider {
    val periodRepository = PeriodRepository(FakePeriodDao(), FakePeriodHistoryDao(), FakePhaseDao())
    val periodHistoryRepository = PeriodHistoryRepository(FakePeriodHistoryDao())
    val seedRepository = SeedRepository(FakeSeedDao(), FakePhaseDao(), FakePeriodDao(), FakeVegetableDao())
    val vegetableRepository = VegetableRepository(FakeVegetableDao())
}