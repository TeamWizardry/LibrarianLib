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

    private var value: T = defaultValue
    protected var callback: ((P) -> T)? = null

    fun getValue(param: P): T {
        val tmp = callback
        if(tmp != null) {
            return tmp(param)
        }
        return value
    }

    fun func(callback: ((P) -> T)?) {
        this.callback = callback
    }

    fun noFunc() {
        this.callback = null
    }

    fun setValue(value: T) {
        this.value = value
    }

}
