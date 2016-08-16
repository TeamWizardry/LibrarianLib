package com.teamwizardry.librarianlib.util.event

import java.util.*

/**
 * Created by TheCodeWarrior
 */
class EventBus {
    private val hooks = mutableMapOf<Class<*>, MutableList<(Event) -> Unit>>().withDefault {mutableListOf()}

    fun <E : Event> fire(event: E) : E {
        val klass = event.javaClass
        if(klass in hooks) {
            if(event.reversed)
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
        hooks[klass]?.add(hook as (Event) -> Unit)
    }
}