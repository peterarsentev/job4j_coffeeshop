package ru.job4j.coffee

/**
 * - Идентификатор заказа
 * - Идентификтор сотрудника
 * - Дата и время
 */
data class OrderEvent(
    val type: EventType, val orderId: Int,
    val employerId: Int, val time : Long = System.currentTimeMillis()
)
