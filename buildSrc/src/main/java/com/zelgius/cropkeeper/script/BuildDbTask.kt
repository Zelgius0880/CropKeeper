package com.zelgius.cropkeeper.script

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

open class BuildDbTask : DefaultTask() {
    @TaskAction
    fun run() {
        Class.forName("org.sqlite.JDBC")
        val file = project.rootProject.project(":app").file("/src/main/assets/initial_db.db")
        if (file.exists()) file.delete()
        if(!file.parentFile.exists()) file.parentFile.mkdirs()
        file.createNewFile()

        var connection: Connection? = null
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")

            val json =
                project.file("schemas/com.zelgius.database.AppDatabase/${Versions.databaseVersion}.json")
                    .bufferedReader()
                    .use {
                        it.readText()
                    }


            val root = JSON.decodeFromString(Root.serializer(), json)

            val statement: Statement = connection.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.

            root.database.entities.forEach {
                statement.executeUpdate(it.createSql.replace("\${TABLE_NAME}", it.tableName))
                it.indices.forEach { index ->
                    statement.executeUpdate(index.createSql.replace("\${TABLE_NAME}", it.tableName))
                }
            }

            ParseCsvFile.parse(statement, project.file("vegetables.csv"))
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    companion object {
        val JSON = Json {
            ignoreUnknownKeys = true
        }
    }
}

@Serializable
data class Root(val database: Database)

@Serializable
data class Database(val entities: List<Entity>)


@Serializable
data class Index(val createSql: String)

@Serializable
data class Entity(val tableName: String, val createSql: String, val indices: List<Index>)