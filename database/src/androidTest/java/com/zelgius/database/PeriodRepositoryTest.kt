package com.zelgius.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zelgius.database.model.Period
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.PhaseRepository
import com.zelgius.database.repository.VegetableRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PeriodRepositoryTest : BaseTest() {
    lateinit var repository: PeriodRepository

    @Before
    fun before() {
        repository = PeriodRepository(periodDao, periodHistoryDao, phaseDao)

        runBlocking {
            val vegetableRepository = VegetableRepository(vegetableDao,fullVegetableDao, seedDao, repository)
            vegetableRepository.insert(*VegetableRepositoryTest.SAMPLE)

            val phaseRepository = PhaseRepository(phaseDao, repository)
            phaseRepository.insert(*PhaseRepositoryTest.SAMPLE)
        }
    }

    @Test
    fun insertAndGet() {
        runBlocking {
            repository.insert(*SAMPLE)
            val list = repository.getAll()
            assertTrue(list.isNotEmpty())
            assertArrayEquals(list.toTypedArray(), SAMPLE)
        }
    }

    @Test
    fun delete() {
        runBlocking {
            repository.insert(*SAMPLE)

            repository.delete(*SAMPLE.sliceArray(0..1))

            val list = repository.getAll()
            assertTrue(list.size == 1)
        }
    }

    @Test
    fun update() {
        runBlocking {
            repository.insert(*SAMPLE)

            val updated =
                SAMPLE.map {
                    it.copy(
                        startingMonth = Random.nextInt(1..12).toFloat(),
                        endingMonth = Random.nextInt(1..12).toFloat()
                    )
                }
                    .toTypedArray()
            repository.update(*updated)

            val list = repository.getAll()
            assertTrue(list.isNotEmpty())
            assertArrayEquals(list.toTypedArray(), updated)
        }
    }

    companion object {
        private val vegetableUids = VegetableRepositoryTest.SAMPLE.map { it.vegetableUid }
        private val phaseUids = PhaseRepositoryTest.SAMPLE.map { it.phaseUid }
        val SAMPLE = arrayOf(
            Period(
                startingMonth = Random.nextInt(1..12).toFloat(),
                endingMonth = Random.nextInt(1..12).toFloat(),
                vegetableUid = vegetableUids.random(),
                phaseUid = phaseUids.random(),
                order = 1
            ),
            Period(
                startingMonth = Random.nextInt(1..12).toFloat(),
                endingMonth = Random.nextInt(1..12).toFloat(),
                vegetableUid = vegetableUids.random(),
                phaseUid = phaseUids.random(),
                order = 2
            ),
            Period(
                startingMonth = Random.nextInt(1..12).toFloat(),
                endingMonth = Random.nextInt(1..12).toFloat(),
                vegetableUid = vegetableUids.random(),
                phaseUid = phaseUids.random(),
                order = 3
            )
        )
    }
}