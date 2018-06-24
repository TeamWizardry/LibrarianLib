package com.teamwizardry.librarianlib.features.math.interpolate

open class InterpCombine<F, S, O>(
        private val first: InterpFunction<F>,
        private val second: InterpFunction<S>,
        private val combine: (F, S) -> O): InterpFunction<O> {
    override fun get(i: Float): O {
        return combine(first.get(i), second.get(i))
    }
}
