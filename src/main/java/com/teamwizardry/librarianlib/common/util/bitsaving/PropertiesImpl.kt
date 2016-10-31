package com.teamwizardry.librarianlib.common.util.bitsaving

import com.teamwizardry.librarianlib.common.util.bitsNeededToStoreNValues

/**
 * Stores an int. Specify a bit depth for the number.
 */
class IntProp(bitCount: Int, val signed: Boolean = false) : BasicBitProp<Int>() {
    val signRegion = PropDataRegion(1)
    val mainRegion = PropDataRegion(bitCount)

    init {
        if(signed)
            dataRegions.put("sign", signRegion)
        dataRegions.put("value", mainRegion)
    }

    override fun get(storage: BitStorage): Int {
        var value = mainRegion.getI(storage)
        if(signed && signRegion.getB(storage))
            value *= -1
        return value
    }

    override fun set(storage: BitStorage, value: Int) {
        mainRegion.setI(storage, Math.abs(value))
        if(signed) signRegion.setB(storage, value < 0)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a float. Specify both the bit depth for the whole number and for the fractional part of the float.
 */
class FloatProp(intBits: Int, decimalBits: Int, val signed: Boolean = false) : BasicBitProp<Float>() {
    val signRegion = PropDataRegion(1)
    val intRegion = PropDataRegion(intBits)
    val decimalRegion = PropDataRegion(decimalBits)
    init {
        if(signed)
            dataRegions.put("sign", signRegion)
        dataRegions.put("int", intRegion)
        dataRegions.put("decimal", decimalRegion)
    }

    val decimalCoefficient = Math.pow(decimalBits.toDouble(), 2.0).toFloat()

    override fun get(storage: BitStorage): Float {
        val intValue = intRegion.getI(storage)
        val decimalValue = decimalRegion.getI(storage)

        return (if(signed && signRegion.getB(storage)) -1 else 1) * (intValue + (decimalValue / decimalCoefficient))
    }

    override fun set(storage: BitStorage, value: Float) {

        if(signed)
            signRegion.setB(storage, value < 0)

        val intValue = Math.abs(value).toInt()
        val decimalValue = (Math.abs(value) * decimalCoefficient).toInt()

        intRegion.setI(storage, intValue)
        decimalRegion.setI(storage, decimalValue)

        storage.notifyIfDirty()
    }
}

/**
 * Stores a reference to a value out of a list. Useful for enums if you use [Enum.values()]
 */
class SelectionProp<T>(val list: Array<T>) : BasicBitProp<T>() {
    val backwardMap = mutableMapOf<T, Int>().withDefault { 0 }
    val mainRegion = PropDataRegion(bitsNeededToStoreNValues(list.size))
    init {
        dataRegions.put("", mainRegion)

        list.forEachIndexed { i, value -> backwardMap.put(value, i) }
    }

    override fun get(storage: BitStorage): T {
        return list[mainRegion.getI(storage)]
    }

    override fun set(storage: BitStorage, value: T) {
        if(value !in backwardMap)
            throw IllegalArgumentException("Value $value not valid")
        mainRegion.setI(storage, backwardMap[value] ?: 0)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a single boolean flag
 */
class BoolProp() : BasicBitProp<Boolean>() {
    val mainRegion = PropDataRegion(1)
    init {
        dataRegions.put("", mainRegion)
    }

    override fun get(storage: BitStorage): Boolean {
        return mainRegion.getB(storage)
    }

    override fun set(storage: BitStorage, value: Boolean) {
        mainRegion.setB(storage, value)
        storage.notifyIfDirty()
    }
}

/**
 * Stores an array of boolean flags
 */
class BoolArrayProp(val size: Int) : BasicArrayBitProp<Boolean>() {
    val mainRegion = PropDataRegion(size)
    init {
        dataRegions.put("", mainRegion)
    }

    override fun get(storage: BitStorage, index: Int): Boolean {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        return mainRegion.getB(storage, index)
    }

    override fun set(storage: BitStorage, index: Int, value: Boolean) {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        mainRegion.setB(storage, value, index)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a map-like construct of boolean keys. Uses a static list of keys internally.
 *
 * @constructor [keys] is the static list of keys
 */
class BoolMapProp<T>(keys: Array<T>) : BasicMapBitProp<T, Boolean>() {
    val size = keys.size
    val map = mutableMapOf<T, Int>()

    val mainRegion = PropDataRegion(size)
    init {
        dataRegions.put("", mainRegion)
        keys.forEachIndexed { index, value ->
            map.put(value, index)
        }
    }

    override fun get(storage: BitStorage, key: T): Boolean {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        return mainRegion.getB(storage, map[key] ?: 0)
    }

    override fun set(storage: BitStorage, key: T, value: Boolean) {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        mainRegion.setB(storage, value, map[key] ?: 0)
        storage.notifyIfDirty()
    }
}

/**
 * Stores an array of ints with a particular bit depth
 *
 * @constructor [bitsPer] is the bit depth of the ints, [size] is the number of values
 */
class IntArrayProp(val bitsPer: Int, val size: Int, val signed: Boolean = false) : BasicArrayBitProp<Int>() {
    val regions = Array<PropDataRegion>(size) {
        PropDataRegion(bitsPer)
    }
    val signRegion = PropDataRegion(size)

    init {
        regions.forEachIndexed { index, region ->
            dataRegions.put("$index", region)
        }
        if(signed)
            dataRegions.put("sign", signRegion)
    }

    override fun get(storage: BitStorage, index: Int): Int {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        return (if(signed && signRegion.getB(storage, index)) -1 else 1) * regions[index].getI(storage)
    }

    override fun set(storage: BitStorage, index: Int, value: Int) {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        regions[index].setI(storage, Math.abs(value))
        if(signed)
            signRegion.setB(storage, value < 0, index)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a map-like construct of int keys. Uses a static list of keys internally.
 *
 * @constructor [bitsPer] is the bit depth of the ints, [keys] is the static list of keys
 */
class IntMapProp<T>(val bitsPer: Int, keys: Array<T>, val signed: Boolean = false) : BasicMapBitProp<T, Int>() {
    val size = keys.size
    val map = mutableMapOf<T, Int>()

    val regions = Array<PropDataRegion>(size) {
        PropDataRegion(bitsPer)
    }
    val signRegion = PropDataRegion(size)
    init {
        regions.forEachIndexed { index, region ->
            dataRegions.put("$index", region)
        }
        if(signed)
            dataRegions.put("sign", signRegion)
        keys.forEachIndexed { index, value ->
            map.put(value, index)
        }
    }

    override fun get(storage: BitStorage, key: T): Int {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        val i = map[key] ?: 0
        return (if(signed && signRegion.getB(storage, i)) -1 else 1) * regions[i].getI(storage)
    }

    override fun set(storage: BitStorage, key: T, value: Int) {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        val i = map[key] ?: 0
        regions[i].setI(storage, Math.abs(value))
        if(signed)
            signRegion.setB(storage, value < 0, i)
        storage.notifyIfDirty()
    }
}

/**
 * Stores an array of ints with a particular bit depth
 *
 * @constructor
 * [intBitsPer] is the bit depth of the whole number section of the values,
 * [decimalBitsPer] is the bit depth of the fractional section of the values,
 * [size] is the number of values
 */
class FloatArrayProp(val intBitsPer: Int, val decimalBitsPer: Int, val size: Int, val signed: Boolean = false) : BasicArrayBitProp<Float>() {
    val intRegions = Array<PropDataRegion>(size) {
        PropDataRegion(intBitsPer)
    }
    val decimalRegions = Array<PropDataRegion>(size) {
        PropDataRegion(decimalBitsPer)
    }
    val signRegion = PropDataRegion(size)

    init {
        intRegions.forEachIndexed { index, region ->
            dataRegions.put("$index-i", region)
        }
        decimalRegions.forEachIndexed { index, region ->
            dataRegions.put("$index-d", region)
        }
        if(signed)
            dataRegions.put("sign", signRegion)
    }

    val decimalCoefficient = Math.pow(decimalBitsPer.toDouble(), 2.0).toFloat()

    override fun get(storage: BitStorage, index: Int): Float {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        val intValue = intRegions[index].getI(storage)
        val decimalValue = decimalRegions[index].getI(storage)

        return (if(signed && signRegion.getB(storage)) -1 else 1) * (intValue + (decimalValue / decimalCoefficient))
    }

    override fun set(storage: BitStorage, index: Int, value: Float) {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        val intValue = Math.abs(value).toInt()
        val decimalValue = (Math.abs(value) * decimalCoefficient).toInt()

        intRegions[index].setI(storage, intValue)
        decimalRegions[index].setI(storage, decimalValue)

        if(signed)
            signRegion.setB(storage, value < 0, index)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a map-like construct of int keys. Uses a static list of keys internally.
 *
 * @constructor
 * [intBitsPer] is the bit depth of the whole number section of the keys,
 * [decimalBitsPer] is the bit depth of the fractional section of the keys,
 * [keys] is the static list of keys
 */
class FloatMapProp<T>(val intBitsPer: Int, val decimalBitsPer: Int, keys: Array<T>, val signed: Boolean = false) : BasicMapBitProp<T, Float>() {
    val size = keys.size
    val map = mutableMapOf<T, Int>()

    val intRegions = Array<PropDataRegion>(size) {
        PropDataRegion(intBitsPer)
    }
    val decimalRegions = Array<PropDataRegion>(size) {
        PropDataRegion(decimalBitsPer)
    }
    val signRegion = PropDataRegion(size)

    init {
        intRegions.forEachIndexed { index, region ->
            dataRegions.put("$index-i", region)
        }
        decimalRegions.forEachIndexed { index, region ->
            dataRegions.put("$index-d", region)
        }
        if(signed)
            dataRegions.put("sign", signRegion)
        keys.forEachIndexed { index, value ->
            map.put(value, index)
        }
    }

    val decimalCoefficient = Math.pow(decimalBitsPer.toDouble(), 2.0).toFloat()

    override fun get(storage: BitStorage, key: T): Float {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        val i = map[key] ?: 0

        val intValue = intRegions[i].getI(storage)
        val decimalValue = decimalRegions[i].getI(storage)

        return (if(signed && signRegion.getB(storage)) -1 else 1) * (intValue + (decimalValue / decimalCoefficient))
    }

    override fun set(storage: BitStorage, key: T, value: Float) {
        if(key !in map.keys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        val i = map[key] ?: 0

        val intValue = Math.abs(value).toInt()
        val decimalValue = (Math.abs(value) * decimalCoefficient).toInt()

        intRegions[i].setI(storage, intValue)
        decimalRegions[i].setI(storage, decimalValue)

        if(signed)
            signRegion.setB(storage, value < 0, i)
        storage.notifyIfDirty()
    }
}

/**
 * Stores an array of ints with a particular bit depth
 *
 * @constructor [values] is the list of valid values, [size] is the number of values
 */
class SelectionArrayProp<T>(val values: Array<T>, val size: Int) : BasicArrayBitProp<T>() {
    val regions = Array<PropDataRegion>(size) {
        PropDataRegion(bitsNeededToStoreNValues(values.size))
    }

    val backwardValues = mutableMapOf<T, Int>()

    init {
        regions.forEachIndexed { index, region ->
            dataRegions.put("$index", region)
        }
        values.forEachIndexed { index, value ->
            backwardValues.put(value, index)
        }
    }

    override fun get(storage: BitStorage, index: Int): T {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")

        return values[regions[index].getI(storage)]
    }

    override fun set(storage: BitStorage, index: Int, value: T) {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        if(value !in backwardValues)
            throw IllegalArgumentException("Value $value isn't valid")

        val valueIndex = backwardValues[value] ?: 0
        regions[index].setI(storage, valueIndex)
        storage.notifyIfDirty()
    }
}

/**
 * Stores a map-like construct of int values. Uses a static list of keys internally.
 *
 * @constructor [keys] is the static list of keys, [values] is the list of valid values
 */
class SelectionMapProp<K, T>(val keys: Array<K>, val values: Array<T>) : BasicMapBitProp<K, T>() {
    val size = keys.size

    val backwardKeys = mutableMapOf<K, Int>()
    val backwardValues = mutableMapOf<T, Int>()

    val regions = Array<PropDataRegion>(size) {
        PropDataRegion(bitsNeededToStoreNValues(values.size))
    }

    init {
        regions.forEachIndexed { index, region ->
            dataRegions.put("$index", region)
        }

        keys.forEachIndexed { index, value ->
            backwardKeys.put(value, index)
        }
        values.forEachIndexed { index, value ->
            backwardValues.put(value, index)
        }
    }

    override fun get(storage: BitStorage, key: K): T {
        if(key !in backwardKeys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")

        val keyIndex = backwardKeys[key] ?: 0
        val valueIndex = regions[keyIndex].getI(storage)
        return values[valueIndex]
    }

    override fun set(storage: BitStorage, key: K, value: T) {
        if(key !in backwardKeys)
            throw IllegalArgumentException("Mapping for key $key doesn't exist")
        if(value !in backwardValues)
            throw IllegalArgumentException("Value $value isn't valid")

        val keyIndex = backwardKeys[key] ?: 0
        val valueIndex = backwardValues[value] ?: 0
        regions[keyIndex].setI(storage, valueIndex)

        storage.notifyIfDirty()
    }
}
