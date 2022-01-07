package com.zelgius.database.dao

import androidx.room.*
import com.zelgius.database.model.Phase

@Dao
interface PhaseDao {
    @Insert
    suspend fun insert(vararg phases: Phase)

    @Update
    suspend fun update(vararg phases: Phase)

    @Delete
    suspend fun delete(vararg phases: Phase)

    @Query("SELECT * FROM phase")
    suspend fun getAll(): List<Phase>

    @Query("SELECT * FROM phase WHERE phase_uid = :uid")
    suspend fun get(uid: String): Phase

}