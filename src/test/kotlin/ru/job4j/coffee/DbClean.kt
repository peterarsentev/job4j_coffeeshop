package ru.job4j.coffee

import org.apache.commons.dbcp2.BasicDataSource
import org.sql2o.Sql2o

class DbClean(source : BasicDataSource) {
    private val sql2o = Sql2o(source)

    fun clean() {
        sql2o.open().use { connection ->
            connection.createQuery(
                "DELETE FROM events;"
            ).executeUpdate()
            connection.createQuery(
                "DELETE FROM last_event;"
            ).executeUpdate()
        }
    }
}