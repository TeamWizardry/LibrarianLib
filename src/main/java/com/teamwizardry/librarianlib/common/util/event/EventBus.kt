package com.teamwizardry.librarianlib.common.util.event

import com.teamwizardry.librarianlib.common.util.lambdainterfs.EventHandler

/**
 * Created by TheCodeWarrior
 */
class EventBus {
    private val hooks = mutableMapOf<Class<*>, MutableList<EventHandler<Event>>>()

    fun hasHooks(clazz: Class<*>): Boolean {
        return hooks[clazz]?.size ?: 0 > 0
    }

    fun <E : Event> fire(event: E): E {
        val clazz = event.javaClass
        if (clazz in hooks) {
            if (event.reversed)
                hooks[clazz]?.asReversed()?.forEach { hook ->
                    hook(event)
                }
            else
                hooks[clazz]?.forEach { hook ->
                    hook(event)
                }
        }
        return event
    }

    fun <E : Event> hook(clazz: Class<E>, hook: (E) -> Unit) {
        hook(clazz, EventHandler(hook))
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> hook(clazz: Class<E>, hook: EventHandler<E>) {
        if (!hooks.containsKey(clazz))
            hooks.put(clazz, mutableListOf())
        hooks[clazz]?.add(hook as EventHandler<Event>)
    }
}
