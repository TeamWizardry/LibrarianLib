package com.teamwizardry.librarianlib.core.util.kotlin

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KMutableProperty0

public fun <T: Any?> threadLocal(): ThreadLocalDelegate<T?> = ThreadLocalDelegate(null)
public fun <T> threadLocal(initial: () -> T): ThreadLocalDelegate<T> = ThreadLocalDelegate(initial)

public class ThreadLocalDelegate<T>(initial: (() -> T)?) {
    private val local = if (initial == null) ThreadLocal<T>() else ThreadLocal.withInitial(initial)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): T = local.get()
    public operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        local.set(value)
    }
}
