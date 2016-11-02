package com.teamwizardry.librarianlib.common.util.bitsaving

import com.teamwizardry.librarianlib.common.util.FakeList
import com.teamwizardry.librarianlib.common.util.FakeMap
import com.teamwizardry.librarianlib.common.util.bitsNeededToStoreNValues

/**
 * Stores an int. Specify a bit depth for the number.
 */
class IntProp(bitCount: Int, val signed: Boolean = false) : PrimitiveBitProp<Int>() {
    val signRegion = PropDataRegion(1)
    val mainRegion = PropDataRegion(bitCount)

    init {
        if(signed)
            dataRegions.put("int_sign", signRegion)
        dataRegions.put("int_value", mainRegion)
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
class FloatProp(intBits: Int, decimalBits: Int, val signed: Boolean = false) : PrimitiveBitProp<Float>() {
    val signRegion = PropDataRegion(1)
    val intRegion = PropDataRegion(intBits)
    val decimalRegion = PropDataRegion(decimalBits)
    init {
        if(signed)
            dataRegions.put("float_sign", signRegion)
        dataRegions.put("float_int", intRegion)
        dataRegions.put("float_decimal", decimalRegion)
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
class SelectionProp<T>(val list: Array<T>) : PrimitiveBitProp<T>() {
    val backwardMap = mutableMapOf<T, Int>().withDefault { 0 }
    val mainRegion = PropDataRegion(bitsNeededToStoreNValues(list.size))
    init {
        dataRegions.put("sel_value", mainRegion)

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
class BoolProp() : PrimitiveBitProp<Boolean>() {
    val mainRegion = PropDataRegion(1)
    init {
        dataRegions.put("bool_value", mainRegion)
    }

    override fun get(storage: BitStorage): Boolean {
        return mainRegion.getB(storage)
    }

    override fun set(storage: BitStorage, value: Boolean) {
        mainRegion.setB(storage, value)
        storage.notifyIfDirty()
    }
}

class ArrayProp<T>(val size: Int, propInitializer: (Int) -> PrimitiveBitProp<T>) : BitProp() {
    val props = (0..size-1).map(propInitializer)

    init {
        props.forEachIndexed { index, prop ->
            prop.dataRegions.forEach {
                dataRegions.put("$index-${it.key}", it.value)
            }
        }
    }

    fun get(storage: BitStorage, index: Int): T {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        return props[index].get(storage)
    }

    fun set(storage: BitStorage, index: Int, value: T) {
        if(index < 0 || index >= size)
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        props[index].set(storage, value)
    }

    override fun delegate(storage: BitStorage): BitStorageValueDelegate<FakeList<T>> {
        return ReadOnlyBitStorageValueDelegate(FakeList(
                { index -> get(storage, index) },
                { index, value -> set(storage, index, value)}
        ))
    }
}

class MapProp<T, K>(val keys: Array<K>, propInitializer: (K) -> PrimitiveBitProp<T>) : BitProp() {
    val backwardsMap = mutableMapOf<K, Int>()
    val props = keys.map(propInitializer)

    init {
        keys.forEachIndexed { i, k -> backwardsMap.put(k, i) }

        props.forEachIndexed { index, prop ->
            prop.dataRegions.forEach {
                dataRegions.put("$index-${it.key}", it.value)
            }
        }
    }

    fun get(storage: BitStorage, key: K): T {
        val index = backwardsMap.get(key) ?: -1
        if(index == -1)
            throw IllegalArgumentException("Key $key not valid")
        return props[index].get(storage)
    }

    fun set(storage: BitStorage, key: K, value: T) {
        val index = backwardsMap.get(key) ?: -1
        if(index == -1)
            throw IllegalArgumentException("Key $key not valid")
        props[index].set(storage, value)
    }

    override fun delegate(storage: BitStorage): BitStorageValueDelegate<FakeMap<K, T>> {
        return ReadOnlyBitStorageValueDelegate(FakeMap(
                { key -> get(storage, key) },
                { key, value -> set(storage, key, value)}
        ))
    }
}
