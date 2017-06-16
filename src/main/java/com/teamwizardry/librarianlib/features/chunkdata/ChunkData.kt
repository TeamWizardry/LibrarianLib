package com.teamwizardry.librarianlib.features.chunkdata

import com.teamwizardry.librarianlib.features.network.PacketCustomChunkData
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.features.saving.SaveInPlace
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraft.world.chunk.Chunk

/**
 * TODO: Document file ChunkData
 *
 * Created by TheCodeWarrior
 */
@SaveInPlace
open class ChunkData(chunk: Chunk) {

    val world = chunk.world
    val pos = chunk.pos
    var name: ResourceLocation? = null
        set(value) {
            if(field != null) throw IllegalStateException("Name already set")
            field = value
        }

    open fun saveCustomNBT(): NBTTagCompound? { return null }
    open fun loadCustomNBT(nbt: NBTTagCompound) {}
    open fun writeCustomBytes(buf: ByteBuf) {}
    open fun readCustomBytes(buf: ByteBuf) {}

    fun resync() {
        if(world is WorldServer) {
            val packet = PacketCustomChunkData(pos, name!!, this)
            world.playerEntities.forEach {
                it as EntityPlayerMP
                if(world.playerChunkMap.isPlayerWatchingChunk(it, pos.chunkXPos, pos.chunkZPos)) {
                    PacketHandler.NETWORK.sendTo(packet, it)
                }
            }
        }
    }

    fun saveToNBT(tag: NBTTagCompound): NBTTagCompound {
        tag.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, false))
        saveCustomNBT()?.also { tag.setTag("custom", it) }
        return tag
    }

    fun loadFromNBT(tag: NBTTagCompound) {
        tag.getTag("auto")?.also { AbstractSaveHandler.readAutoNBT(this, it, false) }
        tag.getCompoundTag("custom")?.also { loadCustomNBT(it) }
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

        private fun getInternal(world: World, chunk: ChunkPos, type: Class<*>): ChunkData? {
            return ChunkWorldData.get(world).containers[chunk]?.datas?.get(type)
        }

        /**
         * Gets the ChunkData for the given world, chunk, and type or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that chunk [ChunkDataRegistry.register] `applyTo` param
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T: ChunkData> get(world: World, chunk: Chunk, type: Class<T>): T? {
            return getInternal(world, chunk.pos, type) as T?
        }

        /**
         * Gets the ChunkData for the given world, chunk position, and type or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that chunk [ChunkDataRegistry.register] `applyTo` param
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T: ChunkData> get(world: World, chunk: ChunkPos, type: Class<T>): T? =
                getInternal(world, chunk, type) as T?

        /**
         * Gets the ChunkData for the given world, chunk, and name or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that chunk [ChunkDataRegistry.register] `applyTo` param
         */
        @JvmStatic
        fun get(world: World, chunk: Chunk, name: ResourceLocation): ChunkData? {
            val clazz = ChunkDataRegistry.get(name)?.clazz ?: return null
            return getInternal(world, chunk.pos, clazz)
        }

        /**
         * Gets the ChunkData for the given world, chunk position, and name or null if the data is not applicable
         *
         * @return the data or null if the data did not apply to that chunk [ChunkDataRegistry.register] `applyTo` param
         */
        @JvmStatic
        fun get(world: World, chunk: ChunkPos, name: ResourceLocation): ChunkData? {
            val clazz = ChunkDataRegistry.get(name)?.clazz ?: return null
            return getInternal(world, chunk, clazz)
        }
    }
}
