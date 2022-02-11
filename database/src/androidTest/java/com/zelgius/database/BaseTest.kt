package com.zelgius.database

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry

abstract class BaseTest {
    val context: Context by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val db: AppDatabase by lazy {
        Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
    }

    val periodDao by lazy { db.periodDao() }
    val vegetableDao by lazy { db.vegetableDao() }
    val phaseDao by lazy { db.phaseDao() }
    val seedDao by lazy { db.seedDao() }
    val periodHistoryDao by lazy { db.periodHistoryDao() }
    val fullSeedDao by lazy { db.fullSeedDao() }
    val fullVegetableDao by lazy { db.fullVegetableDao() }
}