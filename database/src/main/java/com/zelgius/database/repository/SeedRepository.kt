package com.zelgius.database.repository

import com.zelgius.database.dao.PeriodDao
import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.dao.SeedDao
import com.zelgius.database.dao.VegetableDao
import com.zelgius.database.model.*
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SeedRepository @Inject constructor(
    private val seedDao: SeedDao,
    private val phaseDao: PhaseDao,
    private val periodDao: PeriodDao,
    private val vegetableDao: VegetableDao,
) {
    suspend fun insert(vararg seeds: Seed) = seedDao.insert(*seeds)
    suspend fun delete(vararg seeds: Seed) = seedDao.delete(*seeds)
    suspend fun update(vararg seeds: Seed) = seedDao.update(*seeds)

    suspend fun getAll() = seedDao.getAll()

    suspend fun getAllFull(): List<FullSeed> {
        val seeds = seedDao.getAllFull()

        return seeds.map { createFullSeed(it) }
    }

    suspend fun getFull(seed: Seed) = seedDao.get(seed.seedUid)?.let {
        val vegetable = vegetableDao.get(seed.vegetableUid)
        val actualPeriod = periodDao.get(it.actualPeriodUid)

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


    private suspend fun createFullSeed(item: SeedWithVegetableAndPeriod) = FullSeed(
        seed = item.seed,
        vegetable = item.vegetable,
        actualPeriod = PeriodWithPhase(
            period = item.actualPeriod,
            phase = phaseDao.get(item.actualPeriod.phaseUid)
        ),
        periods = periodDao.getPeriodWithPhaseForVegetable(item.vegetable.vegetableUid)
    )
}