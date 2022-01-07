package com.zelgius.database.dao.fake

import com.zelgius.database.dao.SeedDao
import com.zelgius.database.model.Seed
import com.zelgius.database.model.SeedWithVegetable
import com.zelgius.database.model.SeedWithVegetableAndPeriod

class FakeSeedDao : SeedDao {
    override suspend fun insert(vararg seeds: Seed) {}

    override suspend fun update(vararg seeds: Seed) {}

    override suspend fun delete(vararg seeds: Seed) {}

    override suspend fun getAllWithVegetable(): List<SeedWithVegetable> = emptyList()

    override suspend fun getAll(): List<Seed> = seedSample.map { it.seed }
    override suspend fun getAllFull(): List<SeedWithVegetableAndPeriod> = seedSample
    override suspend fun get(uid: String): Seed? =
        seedSample.find { it.seed.seedUid == "uid" }?.seed

}