package com.zelgius.database.repository

import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.model.Phase
import javax.inject.Inject

class PhaseRepository @Inject constructor(
    private val phaseDao: PhaseDao
) {
    suspend fun insert(vararg phases: Phase) = phaseDao.insert(*phases)
    suspend fun delete(vararg phases: Phase) = phaseDao.delete(*phases)
    suspend fun update(vararg phases: Phase) = phaseDao.update(*phases)

    suspend fun getAll() = phaseDao.getAll()

}