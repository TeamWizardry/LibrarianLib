@file:JvmMultifileClass
@file:JvmName("CommonUtilMethods")

package com.teamwizardry.librarianlib.features.kotlin

// Association =========================================================================================================

inline fun <K, V> Iterable<K>.associateInPlace(mapper: (K) -> V) = associate { it to mapper(it) }
inline fun <K, V> Array<K>.associateInPlace(mapper: (K) -> V) = associate { it to mapper(it) }
inline fun <V> BooleanArray.associateInPlace(mapper: (Boolean) -> V) = associate { it to mapper(it) }
inline fun <V> ByteArray.associateInPlace(mapper: (Byte) -> V) = associate { it to mapper(it) }
inline fun <V> ShortArray.associateInPlace(mapper: (Short) -> V) = associate { it to mapper(it) }
inline fun <V> CharArray.associateInPlace(mapper: (Char) -> V) = associate { it to mapper(it) }
inline fun <V> IntArray.associateInPlace(mapper: (Int) -> V) = associate { it to mapper(it) }
inline fun <V> LongArray.associateInPlace(mapper: (Long) -> V) = associate { it to mapper(it) }
inline fun <V> FloatArray.associateInPlace(mapper: (Float) -> V) = associate { it to mapper(it) }
inline fun <V> DoubleArray.associateInPlace(mapper: (Double) -> V) = associate { it to mapper(it) }

inline fun <K, V> Iterable<K>.flatAssociate(mapper: (K) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> Array<out K>.flatAssociate(mapper: (K) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> BooleanArray.flatAssociate(mapper: (Boolean) -> Iterable<Pair<Boolean, V>>): Map<Boolean, V> {
    val map = mutableMapOf<Boolean, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> ByteArray.flatAssociate(mapper: (Byte) -> Iterable<Pair<Byte, V>>): Map<Byte, V> {
    val map = mutableMapOf<Byte, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> ShortArray.flatAssociate(mapper: (Short) -> Iterable<Pair<Short, V>>): Map<Short, V> {
    val map = mutableMapOf<Short, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> CharArray.flatAssociate(mapper: (Char) -> Iterable<Pair<Char, V>>): Map<Char, V> {
    val map = mutableMapOf<Char, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> IntArray.flatAssociate(mapper: (Int) -> Iterable<Pair<Int, V>>): Map<Int, V> {
    val map = mutableMapOf<Int, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> LongArray.flatAssociate(mapper: (Long) -> Iterable<Pair<Long, V>>): Map<Long, V> {
    val map = mutableMapOf<Long, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> FloatArray.flatAssociate(mapper: (Float) -> Iterable<Pair<Float, V>>): Map<Float, V> {
    val map = mutableMapOf<Float, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <V> DoubleArray.flatAssociate(mapper: (Double) -> Iterable<Pair<Double, V>>): Map<Double, V> {
    val map = mutableMapOf<Double, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <T, K, V> Iterable<T>.flatAssociateBy(mapper: (T) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <T, K, V> Array<out T>.flatAssociateBy(mapper: (T) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> BooleanArray.flatAssociateBy(mapper: (Boolean) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> ByteArray.flatAssociateBy(mapper: (Byte) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> ShortArray.flatAssociateBy(mapper: (Short) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> CharArray.flatAssociateBy(mapper: (Char) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> IntArray.flatAssociateBy(mapper: (Int) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> LongArray.flatAssociateBy(mapper: (Long) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> FloatArray.flatAssociateBy(mapper: (Float) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

inline fun <K, V> DoubleArray.flatAssociateBy(mapper: (Double) -> Iterable<Pair<K, V>>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    forEach { map.putAll(mapper(it)) }
    return map
}

