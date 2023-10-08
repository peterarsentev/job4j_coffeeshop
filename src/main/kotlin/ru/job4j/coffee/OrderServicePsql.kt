package ru.job4j.coffee

class OrderServicePsql(private val lastEventStore: LastEventStore, private val store: Store) : OrderService {

    override fun publishEvent(event: OrderEvent) {
        if (event.type == EventType.REG) {
            lastEventStore.create(event.orderId, event.type)
        }
        if (lastEventStore.count(event.orderId) == 0L) {
            throw Exception("Could not type ${event.type} firstly")
        }

        val updated = lastEventStore.update(event.orderId, event.type);
        if (!updated) {
            throw Exception("Could not type ${event.type} after it was done or canceled")
        }
        store.save(event)
    }

    override fun findOrder(orderId: Int): Order {
        val events = store.findByOrderId(orderId)
        if (events.isEmpty()) {
            throw Exception("Order $orderId not found")
        }
        return Order(events)
    }
}