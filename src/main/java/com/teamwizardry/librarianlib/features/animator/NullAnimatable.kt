package com.teamwizardry.librarianlib.features.animator

open class NullAnimatable<T>: IAnimatable<T> {
    override val type: Class<Any> = Any::class.java

    override fun get(target: T): Any {
        return Any()
    }

    override fun set(target: T, value: Any) {
        // nop
    }

    override fun doesInvolve(target: T, obj: Any): Boolean {
        return false
    }
}