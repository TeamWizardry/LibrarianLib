package com.teamwizardry.librarianlib.util.event

import java.util.function.Consumer

/**
 * Created by TheCodeWarrior
 */
class EventBus {
    private val hooks = mutableMapOf<Class<*>, MutableList<EventHandler<Event>>>()

    fun <E : Event> fire(event: E): E {
        val klass = event.javaClass
        if (klass in hooks) {
            if (event.reversed)
                hooks[klass]?.asReversed()?.forEach { hook ->
                    hook(event)
                }
            else
                hooks[klass]?.forEach { hook ->
                    hook(event)
                }
        }
        return event
    }

    fun <E : Event> hook(klass: Class<E>, hook: (E) -> Unit) {
        hook(klass, EventHandler(hook))
    }

    fun <E : Event> hook(klass: Class<E>, hook: EventHandler<E>) {
        if(!hooks.containsKey(klass))
            hooks.put(klass, mutableListOf())
        hooks[klass]?.add(hook as EventHandler<Event>)
    }
}
