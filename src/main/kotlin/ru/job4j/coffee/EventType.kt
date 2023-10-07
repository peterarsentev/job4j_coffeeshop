package ru.job4j.coffee

import com.sun.java.accessibility.util.EventID

enum class EventType(val eventId: Int) {
    REG(0),
    CANCEL(-1),
    IN_PROCESS(1),
    READY(2),
    DONE(3);

}

fun byEventId(eventId: Int): EventType
    = EventType.entries.first {
        eventType -> eventType.eventId == eventId
    }
