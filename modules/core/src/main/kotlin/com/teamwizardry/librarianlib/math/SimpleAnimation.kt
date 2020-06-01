package com.teamwizardry.librarianlib.math

import com.teamwizardry.librarianlib.core.util.lerp.Lerper

class SimpleAnimation<T>(
    private val lerper: Lerper<T>,
    val from: T,
    val to: T,
    duration: Float,
    val easing: Easing
): BasicAnimation<T>(duration) {
    override fun getValueAt(time: Float): T {
        return lerper.lerp(from, to, easing.ease(time))
    }
}