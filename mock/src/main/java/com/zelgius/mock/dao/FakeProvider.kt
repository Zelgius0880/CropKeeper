package com.zelgius.mock.dao

import com.zelgius.database.repository.*

object FakeProvider {
    val periodRepository = PeriodRepository(FakePeriodDao(), FakePeriodHistoryDao(), FakePhaseDao())
    val periodHistoryRepository = PeriodHistoryRepository(FakePeriodHistoryDao())
    val seedRepository = SeedRepository(FakeSeedDao(), FakePhaseDao(), FakePeriodDao(), FakeVegetableDao(), periodRepository)
    val phaseRepository = PhaseRepository(FakePhaseDao())
    val vegetableRepository = VegetableRepository(FakeVegetableDao(), FakeFullVegetableDao(), FakeSeedDao(), periodRepository )
}