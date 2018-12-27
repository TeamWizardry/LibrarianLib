package com.teamwizardry.librarianlib.features.animator

import com.teamwizardry.librarianlib.features.animator.internal.ColorLerper
import com.teamwizardry.librarianlib.features.animator.internal.PrimitiveLerpers
import com.teamwizardry.librarianlib.features.animator.internal.StringLerper
import com.teamwizardry.librarianlib.features.animator.internal.VecLerpers

/**
 * Handles the registering and accessing of [Lerper]s
 */
object LerperHandler {
    private val map = mutableMapOf<Class<*>, Lerper<*>>()

    /**
     * Set the passed class's lerper to the passed lerper
     */
    fun <T> registerLerper(clazz: Class<T>, lerper: Lerper<T>) {
        map[clazz] = lerper
    }

    /**
     * Get the lerper for the passed class, or null if none exists
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getLerper(clazz: Class<T>): Lerper<T>? {
        return map[clazz] as Lerper<T>?
    }

    /**
     * Get the lerper for the passed class, or throw an [IllegalArgumentException]
     */
    fun <T> getLerperOrError(clazz: Class<T>): Lerper<T> {
        return getLerper(clazz) ?: throw IllegalArgumentException("Cannot lerp type `${clazz.canonicalName}`")
    }

    init {
        PrimitiveLerpers
        StringLerper
        VecLerpers
        ColorLerper
    }
}

fun <T> LerperHandler.registerLerper(clazz: Class<T>, lerper: (from: T, to: T, fraction: Float) -> T) {
    this.registerLerper(clazz, object : Lerper<T> {
        override fun lerp(from: T, to: T, fraction: Float): T {
            return lerper(from, to, fraction)
        }
    })
}

/**
 * Handles the lerping (linear interpolating) between values of type [T]
 */
@FunctionalInterface
interface Lerper<T> {
    /**
     * @param from The array at 0
     * @param to The array at 1
     * @param fraction The fractional progress from [from] to [to]. This array **is not** guaranteed to be between 0 and
     *          1. If your lerping algorithm supports it, extrapolate beyond the two input datapoints when the array is
     *          outside of that range. If your algorithm does not support anything outside of 0 and 1, you **must** clamp
     *          this array to that range.
     * @return The interpolated array
     */
    fun lerp(from: T, to: T, fraction: Float): T
}
