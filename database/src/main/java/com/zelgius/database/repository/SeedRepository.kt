package com.zelgius.database.repository

import com.zelgius.database.dao.FullSeedDao
import com.zelgius.database.dao.PeriodHistoryDao
import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.dao.SeedDao
import com.zelgius.database.dao.VegetableDao
import com.zelgius.database.model.FullSeed
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Seed
import com.zelgius.database.model.SeedWithVegetableAndPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SeedRepository @Inject constructor(
    private val seedDao: SeedDao,
    private val phaseDao: PhaseDao,
    private val vegetableDao: VegetableDao,
    private val periodRepository: PeriodRepository,
    private val fullSeedDao: FullSeedDao,
    private val historyRepository: PeriodHistoryDao
) {
    suspend fun insertOrUpdate(vararg seeds: Seed, actualPeriod: Period) {
        seedDao.insert(*seeds)

        historyRepository.insert(
            *seeds.map {
                PeriodHistory(
                    seedUid = it.seedUid,
                    periodUid = actualPeriod.periodUid
                )
            }.toTypedArray()
        )

    }

    suspend fun delete(vararg seeds: Seed) = seedDao.delete(*seeds)
    suspend fun update(vararg seeds: Seed) = seedDao.update(*seeds)

    suspend fun getAll() = seedDao.getAll()

    suspend fun getAllFull(): List<FullSeed> {
        val seeds = seedDao.getAllFull()

        return seeds.map { createFullSeed(it) }
    }

    fun getAllFullFlow(): Flow<List<FullSeed>> = seedDao.getAllFullFlow().map { list ->
        list.map { createFullSeed(it) }
    }

    suspend fun updatePhase(item: FullSeed, phase: Phase) {
        val indexOfPrevious =
            item.periods.indexOfFirst { it.phase.phaseUid == item.actualPeriod.phase.phaseUid }
                .coerceAtLeast(0)
        val indexOfNext = item.periods.indexOfFirst { it.phase.phaseUid == phase.phaseUid }
            .coerceAtLeast(0)

        fullSeedDao.newHistory(
            item.seed,
            item.periods[indexOfPrevious].period,
            item.periods[indexOfNext].period,
            if (indexOfNext != item.periods.size - 1) item.periods.subList(
                indexOfNext + 1,
                item.periods.size
            ).map { it.period }
            else emptyList()
        )
    }

    suspend fun closeSeed(item: FullSeed) {
        fullSeedDao.close(item)
    }

    suspend fun getFull(uid: String) = seedDao.get(uid)?.let {
        val vegetable = vegetableDao.get(it.vegetableUid)
        val actualPeriod = periodRepository.get(it.actualPeriodUid)

        if (vegetable != null && actualPeriod != null) {
            createFullSeed(
                SeedWithVegetableAndPeriod(
                    it,
                    vegetable,
                    actualPeriod
                )
            )
        } else null
    }

    suspend fun delete(seed: Seed) = fullSeedDao.hardDelete(seed)

    private suspend fun createFullSeed(item: SeedWithVegetableAndPeriod) = FullSeed(
        seed = item.seed,
        vegetable = item.vegetable,
        actualPeriod = PeriodWithPhase(
            period = item.actualPeriod,
            phase = phaseDao.get(item.actualPeriod.phaseUid)
        ),
        periods = periodRepository.getPeriodsForVegetable(item.vegetable)
    )
}