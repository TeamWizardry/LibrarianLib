package com.teamwizardry.librarianlib.features.gui

import java.util.function.Function

/**
 * An option that can be defined by setting a value or by a callback
 * @author Pierce Corcoran
 *
 *
 * @param T The type returned
 *
 * @param P The type of the parameter to the option
 */
open class Option<P, T>(defaultValue: T) {

    private var value: T = defaultValue
    protected var callback: ((P) -> T)? = null

    constructor(defaultValue: T, callback: ((P) -> T)?) : this(defaultValue) {
        this.func(callback)
    }

    fun getValue(param: P): T {
        val tmp = callback
        if (tmp != null) {
            return tmp.invoke(param) ?: value
        }
        return value
    }

    fun func(callback: ((P) -> T)?) {
        this.callback = callback
    }

    fun func(callback: Function<P, T>?) {
        func(callback?.let { { t: P -> it.apply(t) } })
    }

    fun noFunc() {
        this.callback = null
    }

    fun setValue(value: T) {
        this.value = value
    }

    operator fun invoke(p: P): T {
        return getValue(p)
    }

    operator fun invoke(t: T) {
        setValue(t)
    }

    operator fun invoke(callback: ((P) -> T)?) {
        func(callback)
    }

}
