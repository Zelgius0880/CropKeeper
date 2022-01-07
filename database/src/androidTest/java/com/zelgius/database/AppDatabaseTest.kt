package com.zelgius.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : BaseTest (){
    @Test
    fun testAssetsRepository() {
        val `is` = InputStreamReader(
            context.assets
                .open("vegetables.csv")
        )

        val reader = BufferedReader(`is`)
        reader.readLine()
        var count = 0
        while (reader.readLine() != null) {
            count ++
        }

        assertTrue(count > 10)
    }
}