package com.zelgius.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zelgius.database.model.Vegetable
import com.zelgius.database.repository.VegetableRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class VegetableRepositoryTest : BaseTest() {
    lateinit var repository: VegetableRepository

    @Before
    fun before() {
        repository = VegetableRepository(vegetableDao)
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

            val updated = SAMPLE.map { it.copy(name = "${it.name} Updated") }.toTypedArray()
            repository.update(*updated)

            val list = repository.getAll()
            assertTrue(list.isNotEmpty())
            assertArrayEquals(list.toTypedArray(), updated)
        }
    }

    companion object {
        val SAMPLE = arrayOf(
            Vegetable(name = "Vegetable 1"),
            Vegetable(name = "Vegetable 2" ),
            Vegetable(name = "Vegetable 3")
        )
    }
}