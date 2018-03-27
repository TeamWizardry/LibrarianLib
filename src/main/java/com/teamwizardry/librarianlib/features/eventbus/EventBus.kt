package com.teamwizardry.librarianlib.features.eventbus

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
        getEventClassList(event.javaClass).forEach { clazz ->
            fire(event, clazz)
        }
        return event
    }

    private fun fire(event: Event, clazz: Class<*>) {
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

    companion object {
        private val classLists = mutableMapOf<Class<*>, List<Class<*>>>()

        private fun getEventClassList(clazz: Class<*>): Iterable<Class<*>> {
            return classLists.getOrPut(clazz) {
                val list = mutableListOf<Class<*>>()

                var c = clazz
                while(Event::class.java.isAssignableFrom(c)) {
                    list.add(c)
                    c = c.superclass ?: break
                }

                return@getOrPut list
            }
        }
    }
}
