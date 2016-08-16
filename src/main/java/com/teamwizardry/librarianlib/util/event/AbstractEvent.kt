package com.teamwizardry.librarianlib.util.event

/**
 * Created by TheCodeWarrior
 */
abstract class AbstractEvent<T> {
    protected val hooks : MutableList<T> = mutableListOf()

    fun hook(hook: T) {
        hooks.add(hook)
    }
}