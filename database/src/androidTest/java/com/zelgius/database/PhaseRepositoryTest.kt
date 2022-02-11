package com.zelgius.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zelgius.database.model.Phase
import com.zelgius.database.repository.PeriodRepository
import com.zelgius.database.repository.PhaseRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PhaseRepositoryTest : BaseTest() {
    lateinit var repository: PhaseRepository

    @Before
    fun before() {
        val periodRepository = PeriodRepository(periodDao, periodHistoryDao, phaseDao)

        repository = PhaseRepository(phaseDao, periodRepository)
    }

    @Test
    fun insertAndGet() {
        runBlocking {
            repository.insert(*SAMPLE)
            val list = repository.getAll()
            Assert.assertTrue(list.isNotEmpty())
            Assert.assertArrayEquals(list.toTypedArray(), SAMPLE)
        }
    }

    @Test
    fun delete() {
        runBlocking {
            repository.insert(*SAMPLE)

            repository.delete(SAMPLE.first())

            val list = repository.getAll()
            Assert.assertTrue(list.size == 1)
        }
    }

    @Test
    fun update() {
        runBlocking {
            repository.insert(*SAMPLE)

            val updated = SAMPLE.map { it.copy(name = "${it.name} Updated") }.toTypedArray()
            repository.update(*updated)

            val list = repository.getAll()
            Assert.assertTrue(list.isNotEmpty())
            Assert.assertArrayEquals(list.toTypedArray(), updated)
        }
    }

    companion object {
        val SAMPLE = arrayOf(Phase(name = "Phase 1", color = "#FFFFFF"), Phase(name = "Phase 2", color = "#FFFFFF"), Phase(name = "Phase 3", color = "#FFFFFF"))
    }
}