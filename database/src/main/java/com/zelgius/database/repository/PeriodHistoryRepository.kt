package com.zelgius.database.repository

import com.zelgius.database.dao.PeriodHistoryDao
import com.zelgius.database.model.PeriodHistory
import com.zelgius.database.model.Vegetable
import javax.inject.Inject

class PeriodHistoryRepository @Inject constructor(
    private val periodHistoryDao: PeriodHistoryDao
) {
    suspend fun insert(vararg histories: PeriodHistory) = periodHistoryDao.insert(*histories)
    suspend fun delete(vararg histories: PeriodHistory) = periodHistoryDao.delete(*histories)
    suspend fun update(vararg histories: PeriodHistory) = periodHistoryDao.update(*histories)

    suspend fun getAll() = periodHistoryDao.getAll()

    suspend fun getYears(vegetable: Vegetable) =
        periodHistoryDao.getHistoryYears(vegetableUid = vegetable.vegetableUid)
}