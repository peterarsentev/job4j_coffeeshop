package ru.job4j.coffee

import org.apache.commons.dbcp2.BasicDataSource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.Exception

class OrderServicePsqlTest {
    private val source = BasicDataSource().apply {
        url = "jdbc:postgresql://127.0.0.1:5432/coffeeshop"
        username = "postgres"
        password = "password"
    }
    @BeforeEach
    fun cleanUp() {
        DbClean(source).clean()
    }

    @Test
    fun whenRegEventCheckSize() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        assertThat(orderService.findOrder(1).eventsSize())
            .isEqualTo(1)
    }

    @Test
    fun whenRegEvent() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.REG)
    }

    @Test
    fun whenRegInProcess() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.IN_PROCESS)
    }

    @Test
    fun whenRegInProcessReady() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        orderService.publishEvent(OrderEvent(EventType.READY,1, 1, 3))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.READY)
    }

    @Test
    fun whenRegInProcessReadyDone() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        orderService.publishEvent(OrderEvent(EventType.READY,1, 1, 3))
        orderService.publishEvent(OrderEvent(EventType.DONE,1, 1, 4))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.DONE)
    }

    @Test
    fun eventNotOrderByReg() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.IN_PROCESS, 1, 1, 1))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type IN_PROCESS firstly")
    }

    @Test
    fun eventAfterDoneEvent() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG, 1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.DONE, 1, 1, 2))
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.CANCEL, 1, 1, 3))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type CANCEL after it was done or canceled")
    }

    @Test
    fun eventAfterCancelEvent() {
        val orderService = OrderServicePsql(LastEventStore(source), DbStore(source))
        orderService.publishEvent(OrderEvent(EventType.REG, 1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.CANCEL, 1, 1, 2))
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.IN_PROCESS, 1, 1, 3))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type IN_PROCESS after it was done or canceled")
    }
}