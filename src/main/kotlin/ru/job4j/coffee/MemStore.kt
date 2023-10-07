package ru.job4j.coffee

class MemStore : Store {
    private val store = HashMap<Int, ArrayList<OrderEvent>>()

    override fun save(event: OrderEvent) {
        store.putIfAbsent(event.orderId, ArrayList());
        store[event.orderId]!!.add(event)
    }

    override fun findByOrderId(orderId: Int): List<OrderEvent>
            = store[orderId] ?: emptyList()
}