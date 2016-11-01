package com.teamwizardry.librarianlib.common.util.bitsaving

import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldSavedData
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by TheCodeWarrior
 */
object BitwiseStorageManager {
    init { MinecraftForge.EVENT_BUS.register(this) }
    private val allocators = mutableMapOf<ResourceLocation, Allocator>()

    fun createStorage(container: IBitStorageContainer,loc: ResourceLocation): BitStorage {
        val value: Allocator = allocators[loc] ?: throw IllegalArgumentException("Allocator for location `$loc` doesn't exist")
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

    private var formatData: BitwiseStorageWorldSavedData? = null
    private var dirty = false

    @SubscribeEvent
    fun worldLoadEvent(event: WorldEvent.Load) {
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

    private var nextIndex = 0

    private fun reloadFormatData() {
        formatData?.let { data ->
            allocators.forEach {
                val (loc, allocator) = it
                val propData = data.formats[loc] ?: mutableMapOf()
                data.formats[loc] = propData

                nextIndex = (propData.values.flatMap { it.values }.map { it.max() }.maxBy { it ?: 0 } ?: -1) + 1
                loadAllocator(propData, allocator)
            }
        }
    }

    private fun loadAllocator(propData: MutableMap<String, MutableMap<String, MutableList<Int>>>, allocator: Allocator) {
        val deadList = propData.getOrPut("~~dead~~", { mutableMapOf()}).getOrPut("", {mutableListOf<Int>()})

        val propertiesToRemove = mutableSetOf<String>()
        val regionsToRemove = mutableSetOf<String>()

        (propData.keys union allocator.props.keys).forEach properties@{ propertyName ->

            val property = allocator.props[propertyName]
            val propertyAllocations = propData[propertyName] ?: mutableMapOf()
            propData[propertyName] = propertyAllocations

            if(property == null) {
                propertiesToRemove.add(propertyName)
                return@properties
            }

            (property.dataRegions.keys union propertyAllocations.keys).forEach { regionName ->

                val region = property.dataRegions[regionName]
                var regionData = propertyAllocations[regionName]

                if (region != null && regionData != null) {
                    val required = region.requiredBits

                    if (required < regionData.size) {
                        dirty = true
                        deadList.addAll(regionData.subList(required, regionData.size))
                        regionData = mutableListOf(*regionData.subList(0, required).toTypedArray())
                        propertyAllocations[regionName] = regionData
                    } else if (required > regionData.size) {
                        regionData.addAll(allocateBits(required - regionData.size))
                    }
                    region.bits = regionData.toIntArray()

                } else if (region == null && regionData != null) {
                    dirty = true
                    deadList.addAll(regionData)
                    regionsToRemove.add(regionName)
                } else if (region != null && regionData == null) {
                    val newList = mutableListOf<Int>()
                    newList.addAll(allocateBits(region.requiredBits))
                    propertyAllocations[regionName] = newList
                    region.bits = newList.toIntArray()
                }
            }

            regionsToRemove.forEach { propertyAllocations.remove(it) }
        }
        propertiesToRemove.forEach { propData.remove(it) }
    }

    private fun allocateBits(amount: Int): Sequence<Int> {
        dirty = true
        val seq = (nextIndex..(nextIndex + amount - 1)).asSequence()
        nextIndex += amount
        return seq
    }

}

class BitwiseStorageWorldSavedData(name: String) : WorldSavedData(name) {
    companion object { val name = "LibLib_BitwiseStorageFormats" }
    val formats = mutableMapOf<ResourceLocation, MutableMap<String, MutableMap<String, MutableList<Int>>>>()

    override fun readFromNBT(nbt: NBTTagCompound) {
        nbt.keySet.forEach { formatName ->
            val format = ResourceLocation(formatName)

            val props = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()
            formats[format] = props

            val formatTag = nbt.getCompoundTag(formatName)
            formatTag.keySet.forEach { propName ->
                val propertyTag = formatTag.getCompoundTag(propName)
                val regions = mutableMapOf<String, MutableList<Int>>()
                props[propName] = regions

                propertyTag.keySet.forEach { regionName ->
                    regions[regionName] = mutableListOf(*propertyTag.getIntArray(regionName).toTypedArray())
                }
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        formats.forEach { format ->
            val formatTag = NBTTagCompound()
            compound.setTag(format.key.toString(), formatTag)

            format.value.forEach { property ->
                val propertyTag = NBTTagCompound()
                formatTag.setTag(property.key, propertyTag)

                property.value.forEach { region ->
                    propertyTag.setIntArray(region.key, region.value.toIntArray())
                }
            }
        }
        return compound
    }
}
