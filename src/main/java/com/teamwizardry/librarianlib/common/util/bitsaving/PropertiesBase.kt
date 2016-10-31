package com.teamwizardry.librarianlib.common.util.bitsaving

import com.teamwizardry.librarianlib.common.util.FakeList
import com.teamwizardry.librarianlib.common.util.FakeMap
import kotlin.reflect.KProperty

/**
 * Created by TheCodeWarrior
 */
abstract class BitStorageValueDelegate<T> {
    abstract fun get(storage: BitStorage): T
    abstract fun set(storage: BitStorage, value: T)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if(thisRef !is IBitStorageContainer)
            throw IllegalStateException("Bit storage properties can only be delegated in instances of IBitStorageContainer")
        set(thisRef.S, value)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if(thisRef !is IBitStorageContainer)
            throw IllegalStateException("Bit storage properties can only be delegated in instances of IBitStorageContainer")
        return get(thisRef.S)
    }
}

class BasicBitStorageValueDelegate<T>(val property: BasicBitProp<T>) : BitStorageValueDelegate<T>() {
    override fun get(storage: BitStorage) = property.get(storage)

    override fun set(storage: BitStorage, value: T) {
        property.set(storage, value)
    }
}

class ReadOnlyBitStorageValueDelegate<T>(val value: T): BitStorageValueDelegate<T>() {
    override fun get(storage: BitStorage) = value

    override fun set(storage: BitStorage, value: T) {
        throw IllegalValueSetException("Can't set value! Read only!")
    }
}

class IllegalValueSetException(message: String) : RuntimeException(message)

abstract class BitProp {
    abstract fun delegate(storage: BitStorage): BitStorageValueDelegate<*>
    val dataRegions: MutableMap<String, PropDataRegion> = mutableMapOf()
}

abstract class BasicBitProp<T> : BitProp() {
    abstract fun get(storage: BitStorage): T
    abstract fun set(storage: BitStorage, value: T)

    protected val delegate = BasicBitStorageValueDelegate(this)
    override fun delegate(storage: BitStorage) = delegate
}

abstract class BasicArrayBitProp<T> : BitProp() {
    abstract fun get(storage: BitStorage, index: Int): T
    abstract fun set(storage: BitStorage, index: Int, value: T)

    override fun delegate(storage: BitStorage): BitStorageValueDelegate<FakeList<T>> {
        return ReadOnlyBitStorageValueDelegate(FakeList(
                { index -> get(storage, index) },
                { index, value -> set(storage, index, value)}
        ))
    }
}

abstract class BasicMapBitProp<K, T> : BitProp() {
    abstract fun get(storage: BitStorage, key: K): T
    abstract fun set(storage: BitStorage, key: K, value: T)

    override fun delegate(storage: BitStorage): BitStorageValueDelegate<FakeMap<K, T>> {
        return ReadOnlyBitStorageValueDelegate(FakeMap(
                { key -> get(storage, key) },
                { key, value -> set(storage, key, value)}
        ))
    }
}

data class PropDataRegion(val requiredBits: Int) {
    var bits: IntArray = intArrayOf()
    fun getI(storage: BitStorage): Int {
        var value = 0

        bits.forEachIndexed { bit, bitIndex ->
            value += if (storage.get(bitIndex)) 1 shl bit else 0
        }

        return value
    }
    fun setI(storage: BitStorage, value: Int) {
        bits.forEachIndexed { bit, bitIndex ->
            storage.set(bitIndex, ((value shr bit) and 1) == 1)
        }
    }

    fun getB(storage: BitStorage, index: Int = 0): Boolean {
        return storage.get(bits[index])
    }
    fun setB(storage: BitStorage, value: Boolean, index: Int = 0) {
        storage.set(bits[index], value)
    }
}
