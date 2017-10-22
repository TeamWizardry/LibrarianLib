package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.animator.internal.PrimitiveLerpers
import com.teamwizardry.librarianlib.features.animator.internal.StringLerper
import com.teamwizardry.librarianlib.features.animator.internal.VecLerpers

/**
 * TODO: Document file Lerping
 *
 * Created by TheCodeWarrior
 */
object LerperHandler {
    private val map = mutableMapOf<Class<*>, Lerper<*>>()

    fun <T> registerLerper(clazz: Class<T>, lerper: Lerper<T>) {
        map[clazz] = lerper
    }

    @Suppress("UNCHECKED_CAST")
    fun getLerper(clazz: Class<*>): Lerper<Any>? {
        return map[clazz] as Lerper<Any>?
    }

    fun getLerperOrError(clazz: Class<*>): Lerper<Any> {
        return getLerper(clazz) ?: throw IllegalArgumentException("Cannot lerp type `${clazz.canonicalName}`")
    }

    init {
        PrimitiveLerpers
        StringLerper
        VecLerpers
    }
}

fun <T> LerperHandler.registerLerper(clazz: Class<T>, lerper: (from: T, to: T, fraction: Float) -> T) {
    this.registerLerper(clazz, object : Lerper<T> {
        override fun lerp(from: T, to: T, fraction: Float): T {
            return lerper(from, to, fraction)
        }
    })
}

@FunctionalInterface
interface Lerper<T> {
    fun lerp(from: T, to: T, fraction: Float): T
}
