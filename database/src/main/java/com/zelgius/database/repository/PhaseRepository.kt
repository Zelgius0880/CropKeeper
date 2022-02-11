package com.zelgius.database.repository

import android.content.Context
import com.zelgius.common.getStringByName
import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.model.Phase
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class PhaseRepository @Inject constructor(
    private val phaseDao: PhaseDao,
    private val periodRepository: PeriodRepository
) {
    suspend fun insert(vararg phases: Phase) = phaseDao.insert(*phases)
    suspend fun delete(phase: Phase) {
        val periods = periodRepository.getPeriodsForPhase(phase)

        if (periods.isEmpty())
            phaseDao.delete(phase)
        else
            phaseDao.update(phase.copy(isDelete = true))
    }

    suspend fun update(vararg phases: Phase) = phaseDao.update(*phases)

    suspend fun getAll() = phaseDao.getAll()


    fun getAll(context: Context? = null) = phaseDao.getAllFlow().transform {
        emit(it.sortedWith { o1, o2 ->
            val s1 = context?.getStringByName(o1.stringResource) ?: o1.name
            val s2 = context?.getStringByName(o2.stringResource) ?: o2.name

            s1.compareTo(s2)
        })
    }

}