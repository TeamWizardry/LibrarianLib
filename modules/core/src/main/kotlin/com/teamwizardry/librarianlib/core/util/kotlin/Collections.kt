@file:Suppress("unused")

package com.teamwizardry.librarianlib.core.util.kotlin

import java.util.Collections
import java.util.IdentityHashMap
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.SortedMap
import java.util.SortedSet
import java.util.TreeMap
import java.util.TreeSet
import java.util.WeakHashMap

// Unmodifiable/synchronized wrappers ==================================================================================

public fun <T> Collection<T>.unmodifiableView(): Collection<T> = Collections.unmodifiableCollection(this)
public fun <T> Set<T>.unmodifiableView(): Set<T> = Collections.unmodifiableSet(this)
public fun <T> SortedSet<T>.unmodifiableView(): SortedSet<T> = Collections.unmodifiableSortedSet(this)
public fun <T> NavigableSet<T>.unmodifiableView(): NavigableSet<T> = Collections.unmodifiableNavigableSet(this)
public fun <T> List<T>.unmodifiableView(): List<T> = Collections.unmodifiableList(this)
public fun <K, V> Map<K, V>.unmodifiableView(): Map<K, V> = Collections.unmodifiableMap(this)
public fun <K, V> SortedMap<K, V>.unmodifiableView(): SortedMap<K, V> = Collections.unmodifiableSortedMap(this)
public fun <K, V> NavigableMap<K, V>.unmodifiableView(): NavigableMap<K, V> = Collections.unmodifiableNavigableMap(this)

public fun <T> Collection<T>.unmodifiableCopy(): Collection<T> = Collections.unmodifiableCollection(this.toList())
public fun <T> Set<T>.unmodifiableCopy(): Set<T> = Collections.unmodifiableSet(this.toSet())
public fun <T> SortedSet<T>.unmodifiableCopy(): SortedSet<T> = Collections.unmodifiableSortedSet(this.toSortedSet(this.comparator()))
public fun <T> NavigableSet<T>.unmodifiableCopy(): NavigableSet<T> = Collections.unmodifiableNavigableSet(TreeSet(this))
public fun <T> List<T>.unmodifiableCopy(): List<T> = Collections.unmodifiableList(this.toList())
public fun <K, V> Map<K, V>.unmodifiableCopy(): Map<K, V> = Collections.unmodifiableMap(this.toMap())
public fun <K, V> SortedMap<K, V>.unmodifiableCopy(): SortedMap<K, V> = Collections.unmodifiableSortedMap(this.toSortedMap(this.comparator()))
public fun <K, V> NavigableMap<K, V>.unmodifiableCopy(): NavigableMap<K, V> = Collections.unmodifiableNavigableMap(TreeMap(this))

public fun <T> MutableCollection<T>.synchronized(): MutableCollection<T> = Collections.synchronizedCollection(this)
public fun <T> MutableSet<T>.synchronized(): MutableSet<T> = Collections.synchronizedSet(this)
public fun <T> SortedSet<T>.synchronized(): SortedSet<T> = Collections.synchronizedSortedSet(this)
public fun <T> NavigableSet<T>.synchronized(): NavigableSet<T> = Collections.synchronizedNavigableSet(this)
public fun <T> MutableList<T>.synchronized(): MutableList<T> = Collections.synchronizedList(this)
public fun <K, V> MutableMap<K, V>.synchronized(): MutableMap<K, V> = Collections.synchronizedMap(this)
public fun <K, V> SortedMap<K, V>.synchronized(): SortedMap<K, V> = Collections.synchronizedSortedMap(this)
public fun <K, V> NavigableMap<K, V>.synchronized(): NavigableMap<K, V> = Collections.synchronizedNavigableMap(this)

// Identity map ========================================================================================================

public fun <K, V> identityMapOf(): MutableMap<K, V> = IdentityHashMap()
public fun <K, V> identityMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> {
    return IdentityHashMap<K, V>(mapCapacity(pairs.size)).apply { putAll(pairs) }
}

public fun <T> identitySetOf(): MutableSet<T> = Collections.newSetFromMap(IdentityHashMap())
public fun <T> identitySetOf(vararg elements: T): MutableSet<T> {
    val map = IdentityHashMap<T, Boolean>(mapCapacity(elements.size))
    return elements.toCollection(Collections.newSetFromMap(map))
}

public fun <K, V> Map<K, V>.toIdentityMap(): MutableMap<K, V> = IdentityHashMap(this)
public fun <T> Set<T>.toIdentitySet(): MutableSet<T> = identitySetOf<T>().also { it.addAll(this) }

public fun <K, V> Map<K, V>.unmodifiableIdentityCopy(): Map<K, V> = Collections.unmodifiableMap(this.toIdentityMap())
public fun <T> Set<T>.unmodifiableIdentityCopy(): Set<T> = Collections.unmodifiableSet(this.toIdentitySet())

// Weak set ============================================================================================================

public fun <T> weakSetOf(): MutableSet<T> = Collections.newSetFromMap(WeakHashMap())
public fun <T> weakSetOf(vararg elements: T): MutableSet<T> {
    val set: MutableSet<T> = Collections.newSetFromMap(WeakHashMap<T, Boolean>(mapCapacity(elements.size)))
    return elements.toCollection(set)
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
