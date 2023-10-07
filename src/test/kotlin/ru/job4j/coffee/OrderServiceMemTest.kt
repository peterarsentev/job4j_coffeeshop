package ru.job4j.coffee

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.lang.Exception

class OrderServiceMemTest {

    @Test
    fun whenRegEvent() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.REG)
    }

    @Test
    fun whenRegInProcess() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.IN_PROCESS)
    }

    @Test
    fun whenRegInProcessReady() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        orderService.publishEvent(OrderEvent(EventType.READY,1, 1, 3))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.READY)
    }

    @Test
    fun whenRegInProcessReadyDone() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG,1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.IN_PROCESS,1, 1, 2))
        orderService.publishEvent(OrderEvent(EventType.READY,1, 1, 3))
        orderService.publishEvent(OrderEvent(EventType.DONE,1, 1, 4))
        assertThat(orderService.findOrder(1).status())
            .isEqualTo(EventType.DONE)
    }

    @Test
    fun eventNotOrderByReg() {
        val orderService = OrderServiceMem(MemStore())
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.IN_PROCESS, 1, 1, 1))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type IN_PROCESS firstly")
    }

    @Test
    fun eventAfterDoneEvent() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG, 1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.DONE, 1, 1, 2))
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.CANCEL, 1, 1, 3))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type CANCEL after it was done or canceled")
    }

    @Test
    fun eventAfterCancelEvent() {
        val orderService = OrderServiceMem(MemStore())
        orderService.publishEvent(OrderEvent(EventType.REG, 1, 1, 1))
        orderService.publishEvent(OrderEvent(EventType.CANCEL, 1, 1, 2))
        assertThatThrownBy {
            orderService.publishEvent(OrderEvent(EventType.IN_PROCESS, 1, 1, 3))
        }.isInstanceOf(Exception::class.java)
            .hasMessageContaining("Could not type IN_PROCESS after it was done or canceled")
    }
}