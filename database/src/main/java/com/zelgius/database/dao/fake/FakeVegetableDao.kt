package com.zelgius.database.dao.fake

import com.zelgius.database.dao.VegetableDao
import com.zelgius.database.model.PeriodWithPhaseAndHistory
import com.zelgius.database.model.Vegetable

class FakeVegetableDao : VegetableDao {
    override suspend fun insert(vararg vegetables: Vegetable) {}

    override suspend fun update(vararg vegetables: Vegetable) {}

    override suspend fun delete(vararg vegetables: Vegetable) {}

    override suspend fun getAllFull(): Map<Vegetable, List<PeriodWithPhaseAndHistory>> = emptyMap()

    override suspend fun getAll(): List<Vegetable> = vegetableSample

    override suspend fun get(uid: String): Vegetable? =
        vegetableSample.find { it.vegetableUid == uid }
}