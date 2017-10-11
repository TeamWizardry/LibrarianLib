package com.teamwizardry.librarianlib.features.chunkdata

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData

/**
 * TODO: Document file ChunkWorldData
 *
 * Created by TheCodeWarrior
 */
class WorldDataContainer(val world: World) : WorldSavedData(NAME) {

    val datas = mutableMapOf<Class<*>, WorldData>()

    companion object {
        val NAME = "librarianlib:worlddata"

        fun get(world: World) : WorldDataContainer {
            return world.perWorldStorage.getOrLoadData(WorldDataContainer::class.java, WorldDataContainer.NAME) as? WorldDataContainer ?:
                    WorldDataContainer(world).also { world.perWorldStorage.setData(WorldDataContainer.NAME, it) }
        }
    }

    init {
        WorldDataRegistry.getApplicable(world).forEach {
            val data = it.constructor(this)
            data.name = it.name
            datas[it.clazz] = data
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val parent = NBTTagCompound()
        compound.setTag("data", parent)
        datas.forEach {
            val name = ChunkDataRegistry.get(it.key)?.name?.toString() ?: return@forEach
            val tag = NBTTagCompound()
            it.value.saveToNBT(tag)
            parent.setTag(name, tag)
        }
        return compound
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        val parent = nbt.getCompoundTag("data")
        datas.forEach {
            val name = ChunkDataRegistry.get(it.key)?.name?.toString() ?: return@forEach
            val tag = parent.getCompoundTag(name)
            it.value.loadFromNBT(tag)
        }
    }
}
