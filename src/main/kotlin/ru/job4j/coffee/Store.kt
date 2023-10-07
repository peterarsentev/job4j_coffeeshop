package ru.job4j.coffee

interface Store {

    fun save(event: OrderEvent)

    fun findByOrderId(orderId: Int) : List<OrderEvent>
}