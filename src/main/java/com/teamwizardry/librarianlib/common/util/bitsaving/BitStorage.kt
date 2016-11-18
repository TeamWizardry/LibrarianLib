package com.teamwizardry.librarianlib.common.util.bitsaving

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import java.util.*

/**
 * Created by TheCodeWarrior
 */

class Allocator internal constructor(val loc: ResourceLocation) {
    val props = mutableMapOf<String, BitProp>()

    fun createProp(name: String, prop: BitProp): Allocator {
        if (name in props.keys)
            throw IllegalStateException("Prop `$name` in allocator `$loc` already exists")
        props[name] = prop
        return this
    }
}

open class BitStorage(val allocator: Allocator, val container: BitStorageContainer) {
    var bitset = BitSet()
        private set

    @Suppress("UNCHECKED_CAST")
    fun <T> getProp(name: String): BitStorageValueDelegate<T> {
        val prop = allocator.props[name] ?: throw IllegalArgumentException("Prop `$name` doesn't exist in allocator `${allocator.loc}`")
        return prop.delegate(this) as BitStorageValueDelegate<T>
    }

    fun writeToNBT(tag: NBTTagCompound, name: String = "m"): NBTTagCompound {
        tag.setByteArray(name, toByteArray())
        return tag
    }

    fun readFromNBT(tag: NBTTagCompound, name: String = "m") {
        readByteArray(tag.getByteArray(name))
    }

    fun toByteArray(): ByteArray = bitset.toByteArray()

    fun readByteArray(array: ByteArray) {
        bitset = BitSet.valueOf(array)
    }

    protected var dirty = false
    protected fun markDirty() {
        dirty = true
    }

    fun notifyIfDirty() {
        if (dirty)
            container.markDirty()
        dirty = false
    }

    operator fun get(bit: Int): Boolean {
        return bitset.get(bit)
    }

    operator fun set(bit: Int, value: Boolean) {
        if (bitset.get(bit) != value)
            markDirty()
        bitset.set(bit, value)
    }
}

interface BitStorageContainer {
    val S: BitStorage
    fun markDirty()
}
