package com.zelgius.mock.dao

import com.zelgius.database.dao.FullVegetableDao
import com.zelgius.database.model.Period
import com.zelgius.database.model.Phase
import com.zelgius.database.model.Vegetable

class FakeFullVegetableDao : FullVegetableDao {
    override suspend fun insert(vararg vegetables: Vegetable) {}
    override suspend fun insert(vararg phases: Phase) {}
    override suspend fun insert(vararg periods: Period) {}
    override suspend fun deletePeriodsNotIn(periodUids: List<String>, vegetableUid: String) {}
    override suspend fun softDeletePeriod(vararg vegetableUid: String) {}
    override suspend fun deletePeriod(vararg vegetableUid: String) {}
    override suspend fun softDeleteVegetable(vararg vegetableUid: String) { }
    override suspend fun deleteVegetable(vararg vegetableUid: String) {}
}