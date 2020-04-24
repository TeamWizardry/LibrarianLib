package com.teamwizardry.librarianlib.facade.value

import kotlin.reflect.KProperty

/**
 * A kotlin delegate that behaves almost identically to a plain `var`, except that it supports implicit animations. It
 * represents a Retained Mode Value, in contrast to [IMValue], which represents an Immediate Mode Value
 */
@Suppress("Duplicates")
class RMValue<T> @JvmOverloads constructor(
    private var value: T, private val change: (oldValue: T, newValue: T) -> Unit = { _, _ -> }
) {

    /**
     * Gets the current value
     */
    fun get(): T {
        return value
    }

    /**
     * Sets a new value
     */
    fun set(value: T) {
        val oldValue = this.value
        this.value = value
        if(oldValue != value) {
            change(oldValue, value)
        }
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return this.get()
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this RMValue
     * (`var property by RMValue()`)
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.set(value)
    }
}

