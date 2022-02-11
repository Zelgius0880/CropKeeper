package com.zelgius.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zelgius.database.model.Seed
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.SeedRepository
import com.zelgius.database.repository.VegetableRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SeedRepositoryTest : BaseTest() {
    lateinit var repository: SeedRepository

    @Before
    fun before() {
        val periodRepository = PeriodRepository(periodDao, periodHistoryDao, phaseDao)
        repository = SeedRepository(seedDao, phaseDao, vegetableDao, periodRepository, fullSeedDao, periodHistoryDao)
        runBlocking {
            val vegetableRepository = VegetableRepository(vegetableDao,fullVegetableDao, seedDao, periodRepository)
            vegetableRepository.insert(*VegetableRepositoryTest.SAMPLE)
        }
    }

    @Test
    fun insertAndGet() {
        runBlocking {
            repository.insertOrUpdate(*SAMPLE, actualPeriod = actualPeriod)
            val list = repository.getAll()
            assertTrue(list.isNotEmpty())
            assertArrayEquals(list.toTypedArray(), SAMPLE)
        }
    }

    @Test
    fun delete() {
        runBlocking {
            repository.insertOrUpdate(*SAMPLE, actualPeriod = actualPeriod)

            repository.delete(*SAMPLE.sliceArray(0..1))

            val list = repository.getAll()
            assertTrue(list.size == 1)
        }
    }

    @Test
    fun update() {
        runBlocking {
            repository.insertOrUpdate(*SAMPLE, actualPeriod = actualPeriod)

            val updated =
                SAMPLE.map { it.copy(startDate = it.startDate.plusDays(Random.nextLong(10))) }
                    .toTypedArray()
            repository.update(*updated)

            val list = repository.getAll()
            assertTrue(list.isNotEmpty())
            assertArrayEquals(list.toTypedArray(), updated)
        }
    }

    companion object {
        private val vegetableUids = VegetableRepositoryTest.SAMPLE.map { it.vegetableUid }
        private val actualPeriod = PeriodRepositoryTest.SAMPLE.first()
        val SAMPLE = arrayOf(
            Seed(
                startDate = LocalDate.now(ZoneId.systemDefault()),
                vegetableUid = vegetableUids.random(),
                actualPeriodUid = actualPeriod.periodUid
            ), Seed(
                startDate = LocalDate.now(ZoneId.systemDefault()),
                actualPeriodUid = actualPeriod.periodUid,
                vegetableUid = vegetableUids.random()
            ), Seed(
                startDate = LocalDate.now(ZoneId.systemDefault()),
                actualPeriodUid = actualPeriod.periodUid,
                vegetableUid = vegetableUids.random()
            )
        )
    }
}