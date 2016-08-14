package com.teamwizardry.librarianlib.gui

import java.util.ArrayList

/**
 * A list of handlers for an event

 * @param  The handler type
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
    fun fireAll(caller: IHandlerCaller<T>) {
        for (t in handlers) {
            caller.call(t)
        }
    }

    /**
     * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
     * @param caller
     */
    fun fireCancel(caller: ICancelableHandlerCaller<T>): Boolean {
        for (t in handlers) {
            if (caller.call(t))
                return true
        }
        return false
    }

    /**
     * Fire an event, each handler will be passed to the caller in order. Once the handler returns true it will halt.
     * @param caller
     */
    fun <V> fireModifier(value: V, caller: IModifierHandlerCaller<V, T>): V {
        var value = value
        for (t in handlers) {
            value = caller.call(t, value)
        }
        return value
    }

    @FunctionalInterface
    interface IHandlerCaller<T> {
        fun call(handler: T)
    }

    @FunctionalInterface
    interface ICancelableHandlerCaller<T> {
        fun call(handler: T): Boolean
    }

    @FunctionalInterface
    interface IModifierHandlerCaller<V, T> {
        fun call(handler: T, value: V): V
    }
}
