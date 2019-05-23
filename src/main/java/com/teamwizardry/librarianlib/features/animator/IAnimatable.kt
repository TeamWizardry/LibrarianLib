package com.teamwizardry.librarianlib.features.animator

/**
 * A property that can be animated.
 */
interface IAnimatable<T> {
    fun get(target: T): Any
    fun set(target: T, value: Any)
    fun doesInvolve(target: T, obj: Any): Boolean

    val type: Class<Any>
}
