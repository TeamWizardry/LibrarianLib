package com.teamwizardry.librarianlib.common.util.bitsaving

import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldSavedData
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
import kotlin.reflect.KProperty

/**
 * Created by TheCodeWarrior
 */
object BitwiseStorageManager {
    init { MinecraftForge.EVENT_BUS.register(this) }
    val allocators = mutableMapOf<ResourceLocation, Allocator>()

    fun createStorage(container: IBitStorageContainer,loc: ResourceLocation): BitStorage {
        var value = allocators[loc]
        if (value == null)
            throw IllegalArgumentException("Allocator for location `$loc` doesn't exist")
        return BitStorage(value, container)
    }

    fun createAllocator(loc: ResourceLocation): Allocator {
        var value = allocators[loc]
        if (value != null)
            throw IllegalStateException("Allocator for location `$loc` already exists")
        value = Allocator(loc)
        allocators[loc] = value
        return value
    }

    var formatData: BitwiseStorageWorldSavedData? = null
    var dirty = false

    @SubscribeEvent
    fun worldLoad(event: WorldEvent.Load) {
        if (event.world is WorldClient)
            return
        if (event.world.provider.dimension == 0) {
            formatData = event.world.mapStorage?.getOrLoadData(BitwiseStorageWorldSavedData::class.java, BitwiseStorageWorldSavedData.name) as BitwiseStorageWorldSavedData?
            if(formatData == null)
                formatData = BitwiseStorageWorldSavedData(BitwiseStorageWorldSavedData.name)
            event.world.mapStorage?.setData(BitwiseStorageWorldSavedData.name, formatData)
            reloadFormatData()
            if(dirty)
                formatData?.markDirty()
            dirty = false
        }
    }

    var nextIndex = 0

    fun reloadFormatData() {
        formatData?.let { data ->
            allocators.forEach {
                val (loc, allocator) = it
                var propData = data.formats[loc]
                if (propData == null) {
                    propData = mutableMapOf()
                    data.formats[loc] = propData
                }

                nextIndex = (propData.values.map { it.max() }.maxBy { it ?: 0 } ?: -1) + 1
                loadAllocator(propData, allocator)

            }
        }
    }

    fun loadAllocator(propData: MutableMap<String, MutableList<Int>>, allocator: Allocator) {
        val deadList = propData.getOrPut("~~dead~~") {mutableListOf<Int>()}
        val toRemove = mutableListOf<String>()

        (propData.keys union allocator.props.keys).forEach { name ->

            val prop = allocator.props[name]
            var bits = propData[name]

            if (prop != null && bits != null) {
                val required = prop.getRequiredBits()

                if (required < bits.size) {
                    dirty = true
                    deadList.addAll(bits.subList(required, bits.size))
                    bits = mutableListOf(*bits.subList(0, required).toTypedArray())
                    propData[name] = bits
                } else if (required > bits.size) {
                    bits.addAll(allocateBits(required - bits.size))

                }
                prop.bits = bits.toIntArray()

            } else if (prop == null && bits != null) {
                dirty = true
                deadList.addAll(bits)
                toRemove.add(name)
            } else if (prop != null && bits == null) {
                val newList = mutableListOf<Int>()
                newList.addAll(allocateBits(prop.getRequiredBits()))
                propData[name] = newList
                prop.bits = newList.toIntArray()
            }
        }
    }

    fun allocateBits(amount: Int): Sequence<Int> {
        dirty = true
        val seq = (nextIndex..(nextIndex + amount - 1)).asSequence()
        nextIndex += amount
        return seq
    }

}

class BitwiseStorageWorldSavedData(name: String) : WorldSavedData(name) {
    companion object { val name = "LibLib_BitwiseStorageFormats" }
    val formats = mutableMapOf<ResourceLocation, MutableMap<String, MutableList<Int>>>()

    override fun readFromNBT(nbt: NBTTagCompound) {
        nbt.keySet.forEach { formatName ->
            val format = ResourceLocation(formatName)

            val props = mutableMapOf<String, MutableList<Int>>()
            formats[format] = props

            val formatTag = nbt.getCompoundTag(formatName)
            formatTag.keySet.forEach { propName ->
                props[propName] = mutableListOf(*formatTag.getIntArray(propName).toTypedArray())
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        formats.forEach { entry ->
            val formatTag = NBTTagCompound()
            compound.setTag(entry.key.toString(), formatTag)

            entry.value.forEach { formatTag.setIntArray(it.key, it.value.toIntArray()) }
        }
        return compound
    }
}

class Allocator internal constructor(val loc: ResourceLocation) {
    val props = mutableMapOf<String, BitProp>()

    fun createProp(name: String, prop: BitProp): Allocator {
        if (name in props.keys)
            throw IllegalStateException("Prop `$name` in allocator `$loc` already exists")
        props[name] = prop
        return this
    }
}

class BitStorage(val allocator: Allocator, val container: IBitStorageContainer) {
    var bitset = BitSet()
        private set

    fun <T> getProp(name: String): BitStorageValueDelegate<T> {
        val prop = allocator.props[name]
        if(prop == null)
            throw IllegalArgumentException("Prop `$name` doesn't exist in allocator `${allocator.loc}`")
        return prop.delegate(this) as BitStorageValueDelegate<T>
    }

    fun writeToNBT(tag: NBTTagCompound, name: String = "m"): NBTTagCompound {
        tag.setByteArray(name, toByteArray())
        return tag
    }

    fun readFromNBT(tag: NBTTagCompound, name: String = "m") {
        readByteArray(tag.getByteArray(name))
    }

    fun toByteArray() = bitset.toByteArray()

    fun readByteArray(array: ByteArray) {
        bitset = BitSet.valueOf(array)
    }

    protected var dirty = false
    protected fun markDirty() {
        dirty = true
    }

    fun notifyIfDirty() {
        if(dirty)
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


interface IBitStorageContainer {
    val S: BitStorage
    fun markDirty()
}
