package com.zelgius.mock.dao

import com.zelgius.database.repository.*

object FakeProvider {
    val periodRepository = PeriodRepository(FakePeriodDao(), FakePeriodHistoryDao(), FakePhaseDao())
    val periodHistoryRepository = PeriodHistoryRepository(FakePeriodHistoryDao())
    val seedRepository = SeedRepository(
        FakeSeedDao(),
        FakePhaseDao(),
        FakeVegetableDao(),
        periodRepository,
        FakeFullSeedDao(),
        FakePeriodHistoryDao()
    )
    val phaseRepository = PhaseRepository(FakePhaseDao(), periodRepository)
    val vegetableRepository = VegetableRepository(
        FakeVegetableDao(),
        FakeFullVegetableDao(),
        FakeSeedDao(),
        periodRepository
    )
}