package ru.job4j.coffee

import org.apache.commons.dbcp2.BasicDataSource;
import org.sql2o.Sql2o
import java.sql.Timestamp

class DbStore(source : BasicDataSource) : Store {
    private val sql2o = Sql2o(source)

    data class OrderEventModel(val id: Int, val event_type: Int,
                               val order_id: Int, val employer_id: Int,
                               val time: Timestamp)

    override fun save(event: OrderEvent) {
        sql2o.open().use { connection ->
            connection.createQuery(
                "INSERT INTO events(event_type, order_id, employer_id) " +
                        "VALUES(:event_type, :order_id, :employer_id)"
            ).addParameter("event_type", event.type.eventId)
                .addParameter("order_id", event.orderId)
                .addParameter("employer_id", event.employerId)
                .executeUpdate()
        }
    }

    override fun findByOrderId(orderId: Int): List<OrderEvent> {
        val result = ArrayList<OrderEvent>()
        sql2o.open().use { connection ->
            val query = connection.createQuery("SELECT * FROM events")
            query.executeAndFetch(OrderEventModel::class.java)
                .forEach {
                    result.add(
                        OrderEvent(
                            byEventId(it.event_type),
                            it.order_id,
                            it.employer_id, it.time.time))
                }
        }
        return result;
    }
}