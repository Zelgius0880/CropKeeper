package com.zelgius.database.dao

import androidx.room.*
import com.zelgius.database.model.*

@Dao
interface SeedDao {
    @Insert
    suspend fun insert(vararg seeds: Seed)

    @Update
    suspend fun update(vararg seeds: Seed)

    @Delete
    suspend fun delete(vararg seeds: Seed)

    @Transaction
    @Query("SELECT * FROM seed")
    suspend fun getAllWithVegetable(): List<SeedWithVegetable>

    @Query("SELECT * FROM seed")
    suspend fun getAll(): List<Seed>

    @Transaction
    @Query(
        "SELECT * FROM seed " +
                "JOIN vegetable ON vegetable.vegetable_uid = seed.vegetable_uid " +
                "JOIN period ON period.period_uid = seed.actual_period_uid "
    )
    suspend fun getAllFull(): List<SeedWithVegetableAndPeriod>

    @Query(
        "SELECT * FROM seed WHERE seed.seed_uid = :uid"
    )
    suspend fun get(uid: String): Seed?
}
