package com.zelgius.database.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.zelgius.database.model.Phase
import kotlinx.coroutines.flow.Flow

@Dao
interface PhaseDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg phases: Phase)

    @Update
    suspend fun update(vararg phases: Phase)

    @Delete
    suspend fun delete(vararg phases: Phase)

    @Query("SELECT * FROM phase")
    suspend fun getAll(): List<Phase>

    @Query("SELECT * FROM phase")
    fun getAllFlow(): Flow<List<Phase>>

    @Query("SELECT * FROM phase WHERE phase_uid = :uid AND is_deleted = 0")
    suspend fun get(uid: String): Phase

}