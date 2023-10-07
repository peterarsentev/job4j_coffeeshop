package ru.job4j.coffee

import java.util.concurrent.ConcurrentHashMap

class OrderServiceMem(private val store: Store) : OrderService {
    private val map = ConcurrentHashMap<Int, EventType>()

    override fun publishEvent(event: OrderEvent) {
        map.computeIfAbsent(event.orderId) { _ ->
            if (event.type != EventType.REG) {
                throw Exception("Could not type ${event.type} firstly")
            }
            event.type
        }
        map.computeIfPresent(event.orderId) { _, value ->
            if (value == EventType.CANCEL || value == EventType.DONE) {
                throw Exception("Could not type ${event.type} after it was done or canceled")
            }
            store.save(event)
            event.type
        }
    }

    override fun findOrder(orderId: Int): Order {
        val events = store.findByOrderId(orderId)
        if (events.isEmpty()) {
            throw Exception("Order $orderId not found")
        }
        return Order(events)
    }
}