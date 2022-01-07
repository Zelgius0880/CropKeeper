package com.zelgius.database.dao.fake

import com.zelgius.database.dao.PhaseDao
import com.zelgius.database.model.*

class FakePhaseDao : PhaseDao {
    override suspend fun insert(vararg phases: Phase) {}

    override suspend fun update(vararg phases: Phase) {}

    override suspend fun delete(vararg phases: Phase) {}

    override suspend fun getAll(): List<Phase> = emptyList()
    override suspend fun get(uid: String): Phase = phaseSample.find { it.phaseUid == uid }!!

}

