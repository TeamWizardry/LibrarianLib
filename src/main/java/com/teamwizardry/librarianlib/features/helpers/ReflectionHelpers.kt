package com.teamwizardry.librarianlib.features.helpers

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.Collections
import java.util.LinkedList

private val allFieldsCache = mutableMapOf<Class<*>, List<Field>>()
private val allMethodsCache = mutableMapOf<Class<*>, List<Method>>()

val Class<*>.allDeclaredFields: List<Field>
    get() = allFieldsCache.getOrPut(this) {
        val list = mutableSetOf<Field>()

        var current: Class<*>? = this
        while(current != null) {
            list.addAll(current.declaredFields)
            current = current.superclass
        }

        return Collections.unmodifiableList(list.toList())
    }

val Class<*>.allDeclaredMethods: List<Method>
    get() = allMethodsCache.getOrPut(this) {
        val list = mutableSetOf<Method>()

        val visited = mutableSetOf<Class<*>>()
        val queue = LinkedList<Class<*>>()
        queue.add(this)
        var current = this
        while(queue.poll()?.also { current = it } != null) {
            queue.addAll(current.interfaces.filter { visited.add(current) })
            if(visited.add(current.superclass)) queue.add(current.superclass)

            list.addAll(current.declaredMethods)
        }

        return Collections.unmodifiableList(list.toList())
    }
