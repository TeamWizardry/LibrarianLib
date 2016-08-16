package com.teamwizardry.librarianlib.util.event

import kotlin.reflect.KCallable

/**
 * Created by TheCodeWarrior
 */
class DataEvent<R, T : KCallable<R>>: AbstractEvent<T>() {

    fun fire(value: R, vararg args: Any?) {
        var currentValue = value

        hooks.forEach {
            currentValue = it.call(currentValue, *args)
        }
    }

}