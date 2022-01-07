package com.zelgius.database.repository

import com.zelgius.database.dao.VegetableDao
import com.zelgius.database.model.Vegetable
import javax.inject.Inject

class VegetableRepository @Inject constructor(
    private val vegetableDao: VegetableDao
) {
    suspend fun insert(vararg vegetables: Vegetable) = vegetableDao.insert(*vegetables)
    suspend fun delete(vararg vegetables: Vegetable) = vegetableDao.delete(*vegetables)
    suspend fun update(vararg vegetables: Vegetable) = vegetableDao.update(*vegetables)

    suspend fun getAll() = vegetableDao.getAll()

}