package com.zelgius.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.zelgius.database.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VegetableDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg vegetables: Vegetable)

    @Update
    suspend fun update(vararg vegetables: Vegetable)

    @Delete
    suspend fun delete(vararg vegetables: Vegetable)

    @Transaction
    @Query("SELECT * FROM vegetable, period")
    suspend fun getAllFull(): Map<Vegetable, List<PeriodWithPhaseAndHistory>>

    @Query("SELECT * FROM vegetable WHERE is_deleted = 0")
    suspend fun getAll(): List<Vegetable>

    @Query("SELECT * FROM vegetable WHERE is_deleted = 0")
    fun getAllFlow(): Flow<List<Vegetable>>

    @Query("SELECT * FROM vegetable WHERE vegetable_uid = :uid")
    suspend fun get(uid: String): Vegetable?
}