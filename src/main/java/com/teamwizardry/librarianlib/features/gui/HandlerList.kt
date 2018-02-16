package com.teamwizardry.librarianlib.features.gui

import java.util.*

/**
 * A list of handlers for an event

 * @param T The handler type
 */
class HandlerList<T> {

    private val handlers = ArrayList<T>()

    /**
     * Add a handler to this event
     */
    fun add(handler: T) {
        handlers.add(handler)
    }

    /**
     * Add a handler to this event at the begining of the list
     */
    fun addFirst(handler: T) {
        handlers.add(0, handler)
    }

    /**
     * Fire an event, each handler will be passed to the caller
     * @param caller
     */
    fun fireAll(caller: (T) -> Unit) {
        for (t in handlers) {
            caller(t)
        }
    }

    /**
     * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
     * @param caller
     */
    fun fireCancel(caller: (T) -> Boolean) = handlers.any { caller(it) }

    /**
     * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
     * @param caller
     */
    fun <V> fireModifier(value: V?, caller: (T, V?) -> V?): V? {
        var v = value
        for (t in handlers) {
            v = caller(t, v)
        }
        return v
    }
}
