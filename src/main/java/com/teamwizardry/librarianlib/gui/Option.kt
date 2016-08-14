package com.teamwizardry.librarianlib.gui

import java.util.function.Function

/**
 * An option that can be defined by setting a value or by a callback
 * @author Pierce Corcoran
 * *
 * *
 * @param  The type returned
 * *
 * @param  The type of the parameter to the option
 */
class Option<P, T>(protected var defaultValue: T) {

    protected var value: T? = null
    protected var callback: Function<P, T>? = null

    fun getValue(param: P): T {
        if (callback == null)
            return if (value == null) defaultValue else value
        return callback!!.apply(param)
    }

    fun func(callback: Function<P, T>) {
        this.callback = callback
    }

    fun noFunc() {
        this.callback = null
    }

    fun setValue(value: T) {
        this.value = value
    }

}
