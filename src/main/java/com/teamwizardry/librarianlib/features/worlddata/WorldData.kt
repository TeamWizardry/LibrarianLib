package com.teamwizardry.librarianlib.features.worlddata

import com.teamwizardry.librarianlib.features.network.PacketCustomWorldData
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.network.TargetWorld
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraft.world.WorldServer

/**
 * TODO: Document file ChunkData
 *
 * Created by TheCodeWarrior
 */
@SaveInPlace
open class WorldData(val container: WorldDataContainer) {

    val world = container.world
    var name: ResourceLocation? = null
        set(value) {
            if (field != null) throw IllegalStateException("Name already set")
            field = value
        }

    protected open fun saveCustomNBT(): NBTTagCompound? {
        return null
    }

    protected open fun loadCustomNBT(nbt: NBTTagCompound) {}
    protected open fun writeCustomBytes(buf: ByteBuf) {}
    protected open fun readCustomBytes(buf: ByteBuf) {}

    fun markDirty() {
        if (world is WorldServer) {
            val packet = PacketCustomWorldData(name!!, this)
            PacketHandler.CHANNEL.update(TargetWorld(world), packet)

            container.markDirty()
        }
    }

    fun saveToNBT(tag: NBTTagCompound): NBTTagCompound {
        tag.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, false))
        saveCustomNBT()?.also { tag.setTag("custom", it) }
        return tag
    }

    fun loadFromNBT(tag: NBTTagCompound) {
        tag.getTag("auto").also { AbstractSaveHandler.readAutoNBT(this, it, false) }
        tag.getCompoundTag("custom").also { loadCustomNBT(it) }
    }

    fun writeToBytes(buf: ByteBuf) {
        AbstractSaveHandler.writeAutoBytes(this, buf, false)
        writeCustomBytes(buf)
    }

    fun readFromBytes(buf: ByteBuf) {
        AbstractSaveHandler.readAutoBytes(this, buf, false)
        readCustomBytes(buf)
    }

    companion object {

        private fun getInternal(world: World, type: Class<*>): WorldData? {
            return WorldDataContainer.get(world).datas.get(type)
        }

        /**
         * Gets the WorldData for the given world and type or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that world [WorldDataRegistry.register] `applyTo` param
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T : WorldData> get(world: World, type: Class<T>): T? =
                getInternal(world, type) as T?

        /**
         * Gets the WorldData for the given world and name or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that world [WorldDataRegistry.register] `applyTo` param
         */
        @JvmStatic
        fun get(world: World, name: ResourceLocation): WorldData? {
            val clazz = WorldDataRegistry.get(name)?.clazz ?: return null
            return getInternal(world, clazz)
        }
    }
}
