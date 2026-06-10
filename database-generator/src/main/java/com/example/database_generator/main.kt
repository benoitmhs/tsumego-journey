package com.example.database_generator

import java.io.File
import java.sql.DriverManager

fun main() {
    val dbFile = File("build/tsumego.db")

    DriverManager.getConnection(
        "jdbc:sqlite:${dbFile.absolutePath}"
    ).use { connection ->

        connection.autoCommit = false

        connection.createStatement().execute(
            "DROP TABLE IF EXISTS tsumego"
        )
        connection.createStatement().execute(
            """
            CREATE TABLE tsumego (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                data TEXT NOT NULL,
                rank TEXT NOT NULL,
                updatedAt INTEGER NOT NULL
        )
        """
        )

        val insert = connection.prepareStatement(
            """
            INSERT INTO tsumego(
                id,
                name,
                data,
                rank,
                updatedAt
            )
            VALUES (?, ?, ?, ?, ?)
        """
        )

        val sgfDir = File("sgf")

        println("read sgfDir: ${sgfDir.absolutePath}")

        sgfDir.walk()
            .filter { it.extension == "sgf" }
            .forEachIndexed { index, file ->

                val tsumego = parseRawTsumego(
                    fileName = file.name,
                    sgfData = file.readText(),
                ) ?: return@forEachIndexed

                insert.setString(1, tsumego.id)
                insert.setString(2, tsumego.name)
                insert.setString(3, tsumego.data)
                insert.setString(4, tsumego.rank.rawValue)
                insert.setLong(5, tsumego.updatedAt.toEpochMilliseconds())

                insert.addBatch()

                if ((index + 1) % 1000 == 0) {
                    insert.executeBatch()
                    println("$index imported")
                }
            }

        connection.commit()
        connection.close()
    }
}