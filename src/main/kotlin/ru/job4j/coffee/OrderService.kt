package ru.job4j.coffee

interface OrderService {
    fun publishEvent(event: OrderEvent)

    fun findOrder(orderId: Int): Order
}