package com.zelgius.mock.dao

import com.zelgius.database.dao.VegetableDao
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.database.model.Vegetable
import com.zelgius.mock.vegetableSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeVegetableDao : VegetableDao {
    override suspend fun insert(vararg vegetables: Vegetable) {}

    override suspend fun update(vararg vegetables: Vegetable) {}

    override suspend fun delete(vararg vegetables: Vegetable) {}

    override suspend fun getAllFull(): Map<Vegetable, List<PeriodWithPhaseAndHistory>> = emptyMap()

    override suspend fun getAll(): List<Vegetable> = vegetableSample
    override fun getAllFlow(): Flow<List<Vegetable>> = flow {
        emit(vegetableSample)
    }

    override suspend fun get(uid: String): Vegetable? =
        vegetableSample.find { it.vegetableUid == uid }
}