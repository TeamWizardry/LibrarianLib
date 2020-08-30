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
        if (clazz in hooks) {
            if (event.reversed)
                hooks[clazz]?.asReversed()?.forEach {
                    it.fire(event)
                }
            else
                hooks[clazz]?.forEach {
                    it.fire(event)
                }
        }
    }

    public inline fun <reified E: Event> hook(hook: Consumer<E>) {
        hook(E::class.java, hook)
    }

    @Suppress("UNCHECKED_CAST")
    public fun <E: Event> hook(clazz: Class<E>, hook: Consumer<E>) {
        if (!hooks.containsKey(clazz))
            hooks.put(clazz, mutableListOf())
        hooks[clazz]?.add(EventHook(hook as Consumer<Event>))
    }

    public fun register(obj: Any) {
        EventHookAnnotationReflector.apply(this, obj)
    }

    private class EventHook(val callback: Consumer<Event>) {
        var data: Any? = null

        fun fire(event: Event) {
            event.hookDataInternal = data
            event.initializeHookStateInternal()
            callback.accept(event)
            event.finalizeHookStateInternal()
            data = event.hookDataInternal
            event.hookDataInternal = null
        }
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

