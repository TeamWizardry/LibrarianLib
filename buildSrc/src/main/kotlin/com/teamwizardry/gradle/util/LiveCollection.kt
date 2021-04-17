package com.teamwizardry.gradle.util

class LiveCollection<T>(private val backingCollection: MutableCollection<T>): Collection<T> by backingCollection {
    private val subscribers = mutableListOf<(T) -> Unit>()

    fun add(value: T) {
        if(backingCollection.add(value)) {
            subscribers.forEach { it(value) }
        }
    }

    operator fun invoke(block: (T) -> Unit) {
        backingCollection.forEach(block)
        subscribers.add(block)
    }
}