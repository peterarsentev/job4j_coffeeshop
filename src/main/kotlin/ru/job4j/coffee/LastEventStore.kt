package ru.job4j.coffee

import org.apache.commons.dbcp2.BasicDataSource
import org.sql2o.Sql2o

class LastEventStore(source : BasicDataSource) {
    private val sql2o = Sql2o(source)

    fun create(orderId: Int, type: EventType) {
        sql2o.open().use { connection ->
            connection.createQuery(
                "INSERT INTO last_event(order_id, event_type) " +
                        "VALUES(:order_id, :event_type) ON CONFLICT DO NOTHING"
            ).addParameter("event_type", type.eventId)
                .addParameter("order_id", orderId)
                .executeUpdate()
        }
    }

    fun update(orderId: Int, type: EventType) : Boolean
        = sql2o.open().use { connection ->
            return@use connection.createQuery(
                "UPDATE last_event SET event_type = :event_type" +
                        "         WHERE order_id = :order_id" +
                        "         AND event_type in (0, 1, 2)"
            ).addParameter("event_type", type.eventId)
                .addParameter("order_id", orderId)
                .executeUpdate().result > 0
        }

    fun count(orderId: Int) : Long {
        sql2o.open().use { connection ->
            val query = connection.createQuery("SELECT count(order_id) FROM last_event " +
                    "WHERE order_id = :order_id")
            query.addParameter("order_id", orderId)
            return query.executeScalar(Long::class.java)
        }
    }
}