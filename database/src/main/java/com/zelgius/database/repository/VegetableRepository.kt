package com.zelgius.database.repository

import android.content.Context
import com.zelgius.common.getStringByName
import com.zelgius.database.AppDatabase
import com.zelgius.database.dao.*
import com.zelgius.database.model.Period
import com.zelgius.database.model.PeriodWithPhase
import com.zelgius.database.model.Vegetable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VegetableRepository @Inject constructor(
    private val vegetableDao: VegetableDao,
    private val fullVegetableDao: FullVegetableDao,
    private val seedDao: SeedDao,
    private val periodRepository: PeriodRepository,
) {
    suspend fun insert(vararg vegetables: Vegetable) = vegetableDao.insert(*vegetables)
    suspend fun delete(vararg vegetables: Vegetable) = vegetableDao.delete(*vegetables)
    suspend fun update(vararg vegetables: Vegetable) = vegetableDao.update(*vegetables)

    fun getAll(context: Context? = null) = vegetableDao.getAllFlow().map {
        it.sortedWith { o1, o2 ->
            val s1 = context?.getStringByName(o1.stringResource)?: o1.name
            val s2 = context?.getStringByName(o2.stringResource)?: o2.name

            s1.compareTo(s2)
        }
    }

    suspend fun insertOrUpdate(vegetable: Vegetable, periods: List<PeriodWithPhase>){
        fullVegetableDao.insert(vegetable, periods)
    }

    suspend fun delete(vegetable: Vegetable): Pair<Vegetable, List<PeriodWithPhase>> {
        val periods = periodRepository.getPeriodsForVegetable(vegetable)
        val seeds = seedDao.getForVegetable(vegetableUid = vegetable.vegetableUid)

        if(seeds.isEmpty()) fullVegetableDao.delete(vegetable)
        else fullVegetableDao.softDelete(vegetable)

        return vegetable to periods
    }
}