package com.teamwizardry.librarianlib.features.worlddata

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.chunkdata.ChunkDataRegistry
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * TODO: Document file ChunkWorldData
 *
 * Created by TheCodeWarrior
 */
@Suppress("UNUSED_PARAMETER")
@Mod.EventBusSubscriber(modid = LibrarianLib.MODID)
class WorldDataContainer(ident: String) : WorldSavedData(NAME) {
    val world: World = gettingWorld
            ?: throw IllegalStateException("WorldDataContainer.gettingWorld is null! Did you not call WorldDataContainer.get()?")

    val datas = mutableMapOf<Class<*>, WorldData>()

    companion object {
        const val NAME = "librarianlib:worlddata"

        @JvmStatic
        @SubscribeEvent
        fun load(e: WorldEvent.Load) {
            get(e.world) // to initialize the data before a user calls get
        }

        fun get(world: World): WorldDataContainer {
            gettingWorld = world
            val wdc = world.perWorldStorage.getOrLoadData(WorldDataContainer::class.java, NAME) as? WorldDataContainer ?:
                    WorldDataContainer(NAME).also { world.perWorldStorage.setData(NAME, it) }
            gettingWorld = null
            return wdc
        }

        private var gettingWorld: World? by threadLocal()
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
