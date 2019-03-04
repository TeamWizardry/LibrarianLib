package com.teamwizardry.librarianlib.features.kotlin

import java.util.Collections
import java.util.IdentityHashMap
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.SortedMap
import java.util.SortedSet

// Unmodifiable/synchronized wrappers ==================================================================================

fun <T> Collection<T>.unmodifiableView() = Collections.unmodifiableCollection(this)
fun <T> Set<T>.unmodifiableView() = Collections.unmodifiableSet(this)
fun <T> SortedSet<T>.unmodifiableView() = Collections.unmodifiableSortedSet(this)
fun <T> NavigableSet<T>.unmodifiableView() = Collections.unmodifiableNavigableSet(this)
fun <T> List<T>.unmodifiableView() = Collections.unmodifiableList(this)
fun <K, V> Map<K, V>.unmodifiableView() = Collections.unmodifiableMap(this)
fun <K, V> SortedMap<K, V>.unmodifiableView() = Collections.unmodifiableSortedMap(this)
fun <K, V> NavigableMap<K, V>.unmodifiableView() = Collections.unmodifiableNavigableMap(this)
fun <T> Collection<T>.synchronized() = Collections.synchronizedCollection(this)
fun <T> Set<T>.synchronized() = Collections.synchronizedSet(this)
fun <T> SortedSet<T>.synchronized() = Collections.synchronizedSortedSet(this)
fun <T> NavigableSet<T>.synchronized() = Collections.synchronizedNavigableSet(this)
fun <T> List<T>.synchronized() = Collections.synchronizedList(this)
fun <K, V> Map<K, V>.synchronized() = Collections.synchronizedMap(this)
fun <K, V> SortedMap<K, V>.synchronized() = Collections.synchronizedSortedMap(this)
fun <K, V> NavigableMap<K, V>.synchronized() = Collections.synchronizedNavigableMap(this)

fun <T> Collection<T>.unmodifiableCopy() = Collections.unmodifiableCollection(this.toList())
fun <T> Set<T>.unmodifiableCopy() = Collections.unmodifiableSet(this.toSet())
fun <T> List<T>.unmodifiableCopy() = Collections.unmodifiableList(this.toList())
fun <K, V> Map<K, V>.unmodifiableCopy() = Collections.unmodifiableMap(this.toMap())

// Identity map ========================================================================================================

fun <K, V> identityMapOf(): MutableMap<K, V> = IdentityHashMap()
fun <K, V> identityMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> {
    return IdentityHashMap<K, V>(mapCapacity(pairs.size)).apply { putAll(pairs) }
}

fun <T> identitySetOf(): MutableSet<T> = Collections.newSetFromMap(IdentityHashMap())
fun <T> identitySetOf(vararg elements: T): MutableSet<T> {
    val map = IdentityHashMap<T, Boolean>(mapCapacity(elements.size))
    return elements.toCollection(Collections.newSetFromMap(map))
}

// Private utils =======================================================================================================

// ripped from the Kotlin runtime:
private fun mapCapacity(expectedSize: Int): Int {
    if (expectedSize < 3) {
        return expectedSize + 1
    }
    if (expectedSize < INT_MAX_POWER_OF_TWO) {
        return expectedSize + expectedSize / 3
    }
    return Int.MAX_VALUE // any large value
}

private const val INT_MAX_POWER_OF_TWO: Int = Int.MAX_VALUE / 2 + 1
