package com.teamwizardry.librarianlib.core.util.kotlin

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KMutableProperty0

operator fun <T> KProperty0<T>.getValue(thisRef: Any, property: KProperty<*>): T {
    return this.get()
}

operator fun <T> KMutableProperty0<T>.setValue(thisRef: Any, property: KProperty<*>, value: T) {
    this.set(value)
}

fun <T : Any?> threadLocal() = ThreadLocalDelegate<T?>(null)
fun <T> threadLocal(initial: () -> T) = ThreadLocalDelegate(initial)

class ThreadLocalDelegate<T>(initial: (() -> T)?) {
    private val local = if (initial == null) ThreadLocal<T>() else ThreadLocal.withInitial(initial)

    operator fun getValue(thisRef: Any, property: KProperty<*>): T = local.get()
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) = local.set(value)
}
