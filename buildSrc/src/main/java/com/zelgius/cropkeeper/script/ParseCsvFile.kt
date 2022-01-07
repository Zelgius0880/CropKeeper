package com.zelgius.cropkeeper.script

import org.gradle.api.DefaultTask
import java.io.File
import java.sql.Statement
import java.util.*

object ParseCsvFile {
    fun parse(statement: Statement, csv: File) {
        csv.bufferedReader()
            .use { br ->
                var line: String? = null
                while (br.readLine()?.let { l -> line = l } != null) {
                    line?.split(",")?.let {
                        val vegetableUid = statement.createVegetableIfNeeded(it.first())
                        val phaseUid = statement.createPhaseIfNeeded(it.last())
                        statement.createPeriod(
                            vegetableUid,
                            phaseUid,
                            it[1].toFloat(),
                            it[2].toFloat()
                        )
                    }
                }
            }
    }

    private fun Statement.createVegetableIfNeeded(name: String): String {
        val rs = executeQuery("SELECT vegetable_uid FROM vegetable WHERE name = '${name}'")
        return if (rs.next()) rs.getString(1)
        else {
            val uid = UUID.randomUUID().toString()

            executeUpdate(
                "INSERT INTO vegetable (vegetable_uid, name, is_deleted, drawable_resource, string_resource) " +
                        "VALUES ('$uid', '$name', false, " +
                        "'${
                            name.toLowerCase(Locale.getDefault()).split("(").first().trim()
                                .replace(" ", "_")
                        }', " +
                        "'${
                            name.toLowerCase(Locale.getDefault()).replace("(", "").replace(")", "")
                                .replace(" ", "_")
                        }')"
            )
            uid
        }
    }

    private fun Statement.createPhaseIfNeeded(name: String): String {
        val rs = executeQuery("SELECT phase_uid FROM phase WHERE name = '${name}'")
        return if (rs.next()) rs.getString(1)
        else {
            val uid = UUID.randomUUID().toString()

            val color = colors[uid.sumOf { it.toByte().toInt() } % colors.size]

            executeUpdate(
                "INSERT INTO phase (phase_uid, name, is_deleted, color, string_resource) " +
                        "VALUES ('$uid', '$name', false, '$color', '${
                            name.toLowerCase(Locale.getDefault()).replace(" ", "_")
                        }')"
            )
            uid
        }
    }

    private fun Statement.createPeriod(
        vegetableUid: String,
        phaseUid: String,
        startMonth: Float,
        endMonth: Float
    ) {
        val rs =
            executeQuery("SELECT count(*) FROM period p JOIN vegetable v ON v.vegetable_uid = p.vegetable_uid WHERE v.vegetable_uid = '${vegetableUid}'")
        val count = if (rs.next()) rs.getInt(1)
        else {
            0
        }

        executeUpdate(
            "INSERT INTO period (period_uid, starting_month, ending_month, is_deleted, 'order', phase_uid, vegetable_uid) " +
                    "VALUES ('${UUID.randomUUID()}', $startMonth, $endMonth , false, $count, '$phaseUid', '$vegetableUid')"
        )
    }

    private val colors = listOf(
        "#D50000",
        "#C51162",
        "#AA00FF",
        "#6200EA",
        "#304FFE",
        "#2962FF",
        "#0091EA",
        "#00B8D4",
        "#00BFA5",
        "#00C853",
        "#64DD17",
        "#AEEA00",
        "#FFD600",
        "#FFAB00",
        "#FF6D00",
        "#DD2600",
    )
}