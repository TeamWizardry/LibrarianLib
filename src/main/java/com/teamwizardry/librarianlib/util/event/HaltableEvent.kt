package com.teamwizardry.librarianlib.util.event

import kotlin.reflect.KCallable

/**
 * Created by TheCodeWarrior
 */
class HaltableEvent<T : KCallable<Boolean>> : AbstractEvent<T>() {
    fun fire(vararg args: Any?) : Boolean{
        hooks.forEach {
            if(it.call(args))
                return true
        }
        return false
    }
}