package com.zelgius.database.dao

import androidx.room.*
import com.zelgius.database.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VegetableDao {
    @Insert
    suspend fun insert(vararg vegetables: Vegetable)

    @Update
    suspend fun update(vararg vegetables: Vegetable)

    @Delete
    suspend fun delete(vararg vegetables: Vegetable)

    @Transaction
    @Query("SELECT * FROM vegetable, period")
    suspend fun getAllFull(): Map<Vegetable, List<PeriodWithPhaseAndHistory>>

    @Query("SELECT * FROM vegetable")
    suspend fun getAll(): List<Vegetable>

    @Query("SELECT * FROM vegetable WHERE vegetable_uid = :uid")
    suspend fun get(uid: String): Vegetable?
}