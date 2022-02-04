package com.zelgius.database.repository

import android.content.Context
import com.zelgius.common.getStringByName
import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.model.Phase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhaseRepository @Inject constructor(
    private val phaseDao: PhaseDao
) {
    suspend fun insert(vararg phases: Phase) = phaseDao.insert(*phases)
    suspend fun delete(vararg phases: Phase) = phaseDao.delete(*phases)
    suspend fun update(vararg phases: Phase) = phaseDao.update(*phases)

    suspend fun getAll() = phaseDao.getAll()

    suspend fun inertOrUpdate(phase: Phase) {
        phaseDao.insert(phase)
    }

    fun getAll(context: Context? = null) = flow {
        emit(phaseDao.getAll().sortedWith { o1, o2 ->
            val s1 = context?.getStringByName(o1.stringResource) ?: o1.name
            val s2 = context?.getStringByName(o2.stringResource) ?: o2.name

            s1.compareTo(s2)
        }
        )
    }

}