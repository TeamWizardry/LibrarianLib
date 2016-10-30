package com.teamwizardry.librarianlib.common.util.bitsaving

/**
 * Created by TheCodeWarrior
 */
abstract class BitProp {
    abstract fun getRequiredBits(): Int
    abstract fun delegate(storage: BitStorage): BitStorageValueDelegate<*>
    var bits: IntArray = intArrayOf()
}

abstract class BasicBitProp<T> : BitProp() {
    abstract fun get(storage: BitStorage): T
    abstract fun set(storage: BitStorage, value: T)

    protected val delegate = BasicBitStorageValueDelegate(this)
    override fun delegate(storage: BitStorage) = delegate
}

class IntProp(val bitCount: Int) : BasicBitProp<Int>() {
    override fun get(storage: BitStorage): Int {
        var value = 0

        bits.forEachIndexed { bit, index ->
            value += if (storage.get(index)) 1 shl bit else 0
        }

        return value
    }

    override fun set(storage: BitStorage, value: Int) {
        bits.forEachIndexed { bit, index ->
            storage.set(index, ((value shr bit) and 1) == 1)
        }
        storage.notifyIfDirty()
    }

    override fun getRequiredBits() = bitCount



}
