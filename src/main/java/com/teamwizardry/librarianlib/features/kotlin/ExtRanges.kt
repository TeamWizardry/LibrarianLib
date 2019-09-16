package com.teamwizardry.librarianlib.features.kotlin

private class DoubleProgression(
    private val start: Double,
    private val endInclusive: Double,
    private val step: Double
) : Iterable<Double> {
    init {
        if (step == 0.0) throw kotlin.IllegalArgumentException("Step must be non-zero.")
    }

    override fun iterator(): DoubleIterator = DoubleProgressionIterator()

    private inner class DoubleProgressionIterator() : DoubleIterator() {
        private var hasNext: Boolean = if (step > 0) endInclusive >= start else start >= endInclusive
        private var next = start

        override fun hasNext(): Boolean = hasNext

        override fun nextDouble(): Double {
            val value = next
            next += step
            if ((step > 0 && next > endInclusive) || (step < 0 && next < endInclusive)) {
                if (!hasNext) throw kotlin.NoSuchElementException()
                hasNext = false
            }
            else {
                next += step
            }
            return value
        }
    }
}

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    return DoubleProgression(start, endInclusive, step)
}

private class FloatProgression(
    private val start: Float,
    private val endInclusive: Float,
    private val step: Float
) : Iterable<Float> {
    init {
        if (step == 0f) throw kotlin.IllegalArgumentException("Step must be non-zero.")
    }

    override fun iterator(): FloatIterator = FloatProgressionIterator()

    private inner class FloatProgressionIterator() : FloatIterator() {
        private var hasNext: Boolean = if (step > 0) endInclusive >= start else start >= endInclusive
        private var next = start

        override fun hasNext(): Boolean = hasNext

        override fun nextFloat(): Float {
            val value = next
            next += step
            if ((step > 0 && next > endInclusive) || (step < 0 && next < endInclusive)) {
                if (!hasNext) throw kotlin.NoSuchElementException()
                hasNext = false
            }
            else {
                next += step
            }
            return value
        }
    }
}

infix fun ClosedRange<Float>.step(step: Float): Iterable<Float> {
    return FloatProgression(start, endInclusive, step)
}
