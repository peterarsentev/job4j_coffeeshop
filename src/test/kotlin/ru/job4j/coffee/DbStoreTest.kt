package ru.job4j.coffee

import org.apache.commons.dbcp2.BasicDataSource
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DbStoreTest {

    @Test
    fun whenSaveAndFetch() {
        val source = BasicDataSource()
        source.url = "jdbc:postgresql://127.0.0.1:5432/coffeeshop"
        source.username = "postgres"
        source.password = "password"
        val store = DbStore(source)
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        store.save(OrderEvent(EventType.REG,1, 1))
        Assertions.assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.REG)
    }
}