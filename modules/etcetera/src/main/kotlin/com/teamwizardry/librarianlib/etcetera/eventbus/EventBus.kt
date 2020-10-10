package com.teamwizardry.librarianlib.etcetera.eventbus

import java.util.function.Consumer

public class EventBus {
    private var hooks = mutableMapOf<Class<*>, MutableList<EventHook>>()

    public fun hasHooks(clazz: Class<*>): Boolean {
        return hooks[clazz]?.size ?: 0 > 0
    }

    public fun <E: Event> fire(event: E): E {
        getEventClassList(event.javaClass).forEach { clazz ->
            fire(event, clazz)
        }
        return event
    }

    private fun fire(event: Event, clazz: Class<*>) {
        hooks[clazz]?.forEach { hook ->
            hook.fire(event)
        }
    }

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     * @param priority the hook priority
     * @param receiveCanceled whether the hook should still receive canceled events
     */
    public inline fun <reified E: Event> hook(priority: Priority, receiveCanceled: Boolean, hook: Consumer<E>) {
        hook(E::class.java, priority, receiveCanceled, hook)
    }

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     * @param priority the hook priority
     */
    public inline fun <reified E: Event> hook(priority: Priority, hook: Consumer<E>) {
        // manual overloads because type inference for method references doesn't play well with default parameters
        hook(E::class.java, priority, false, hook)
    }

    /**
     * Hook into `T` events (or any subclass of `T`).
     *
     * @param hook the event consumer
     */
    public inline fun <reified E: Event> hook(hook: Consumer<E>) {
        // manual overloads because type inference for method references doesn't play well with default parameters
        hook(E::class.java, Priority.DEFAULT, false, hook)
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param priority the hook priority
     * @param receiveCanceled whether the hook should still receive canceled events
     * @param hook the event consumer
     */
    @Suppress("UNCHECKED_CAST")
    public fun <E: Event> hook(
        clazz: Class<E>,
        priority: Priority,
        receiveCanceled: Boolean,
        hook: Consumer<E>,
    ) {
        hooks.getOrPut(clazz) { mutableListOf() }.also { eventHooks ->
            eventHooks.add(EventHook(hook as Consumer<Event>, priority, receiveCanceled))
            eventHooks.sort()
        }
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param priority the hook priority
     * @param hook the event consumer
     */
    public fun <E: Event> hook(clazz: Class<E>, priority: Priority, hook: Consumer<E>) {
        this.hook(clazz, priority, false, hook)
    }

    /**
     * Hook into [clazz] events (or any subclass of [clazz]).
     *
     * @param clazz the event type to hook into
     * @param hook the event consumer
     */
    public fun <E: Event> hook(clazz: Class<E>, hook: Consumer<E>) {
        this.hook(clazz, Priority.DEFAULT, false, hook)
    }

    public fun register(obj: Any) {
        EventHookAnnotationReflector.apply(this, obj)
    }

    private class EventHook(val callback: Consumer<Event>, val priority: Priority, val receiveCanceled: Boolean): Comparable<EventHook> {
        var state: Any? = null

        fun fire(event: Event) {
            if(!receiveCanceled && event is CancelableEvent && event.isCanceled())
                return
            event.loadPerHookStateInternal(state)
            callback.accept(event)
            state = event.storePerHookStateInternal()
        }

        override fun compareTo(other: EventHook): Int {
            return this.priority.compareTo(other.priority)
        }
    }

    public enum class Priority {
        /**
         * The highest priority. All the hooks with this priority will run first
         */
        FIRST,

        /**
         * The second highest priority. All the hooks with this priority will run after those with [FIRST] priority.
         */
        EARLY,

        /**
         * The default priority. All the hooks with this priority will run after those with [EARLY] priority.
         */
        DEFAULT,

        /**
         * The second lowest priority. All the hooks with this priority will run after those with [DEFAULT] priority.
         */
        LATE,

        /**
         * The lowest priority. All the hooks with this priority will run after those with [LATE] priority.
         */
        LAST,
    }

    public companion object {
        private val classLists = mutableMapOf<Class<*>, List<Class<*>>>()

        private fun getEventClassList(clazz: Class<*>): Iterable<Class<*>> {
            return classLists.getOrPut(clazz) {
                val list = mutableListOf<Class<*>>()

                var c = clazz
                while (Event::class.java.isAssignableFrom(c)) {
                    list.add(c)
                    c = c.superclass ?: break
                }

                list
            }
        }
    }
}

