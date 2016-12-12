package com.teamwizardry.librarianlib.common.util.event

import java.util.function.Consumer

/**
 * Created by TheCodeWarrior
 */
class EventBus {
    private val hooks = mutableMapOf<Class<*>, MutableList<Consumer<Event>>>()

    fun hasHooks(clazz: Class<*>): Boolean {
        return hooks[clazz]?.size ?: 0 > 0
    }

    fun <E : Event> fire(event: E): E {
        val clazz = event.javaClass
        if (clazz in hooks) {
            if (event.reversed)
                hooks[clazz]?.asReversed()?.forEach {
                    it.accept(event)
                }
            else
                hooks[clazz]?.forEach {
                    it.accept(event)
                }
        }
        return event
    }

    fun <E : Event> hook(clazz: Class<E>, hook: (E) -> Unit) {
        hook(clazz, Consumer(hook))
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> hook(clazz: Class<E>, hook: Consumer<E>) {
        if (!hooks.containsKey(clazz))
            hooks.put(clazz, mutableListOf())
        hooks[clazz]?.add(hook as Consumer<Event>)
    }
}
