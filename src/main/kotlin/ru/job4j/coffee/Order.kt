package ru.job4j.coffee

class Order(private val events: List<OrderEvent>) {
    fun status() : EventType = events.get(events.size - 1).type

    fun eventsSize() : Int = events.size
}
