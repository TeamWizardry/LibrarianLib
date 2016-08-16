package com.teamwizardry.librarianlib.util.event

import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

/**
 * Created by TheCodeWarrior
 */
class Event<T : KCallable<Unit>> : AbstractEvent<T>() {

    fun fire(vararg args: Any?) {
        hooks.forEach {
            it.call(args)
        }
    }

}