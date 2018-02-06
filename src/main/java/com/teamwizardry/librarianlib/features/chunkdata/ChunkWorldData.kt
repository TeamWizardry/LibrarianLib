package com.teamwizardry.librarianlib.features.chunkdata

import com.teamwizardry.librarianlib.features.network.PacketCustomChunkData
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.ChunkDataEvent
import net.minecraftforge.event.world.ChunkEvent
import net.minecraftforge.event.world.ChunkWatchEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * TODO: Document file ChunkWorldData
 *
 * Created by TheCodeWarrior
 */
class ChunkWorldData : WorldSavedData(NAME) {

    val containers = mutableMapOf<ChunkPos, ChunkDataContainer>()

    companion object {
        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        const val NAME = "librarianlib:chunkdata"

        @SubscribeEvent
        fun loadChunk(e: ChunkDataEvent.Load) {
            val rootData = e.data.getCompoundTag("librarianlib_chunkdata") ?: NBTTagCompound()

            val container = ChunkDataContainer()
            ChunkDataRegistry.getApplicable(e.chunk).forEach {
                val data = it.constructor(e.chunk)
                data.name = it.name
                if(rootData.hasKey(it.name.toString())) {
                    val nbt = rootData.getCompoundTag(it.name.toString())
                    data.loadFromNBT(nbt)
                } else {
                    data.initializeNewChunk()
                }
                container.datas[it.clazz] = data
            }

            get(e.chunk.world).containers[e.chunk.pos] = container
        }

        @SubscribeEvent
        fun saveChunk(e: ChunkDataEvent.Save) {
            val rootData = NBTTagCompound()

            val container = get(e.chunk.world).containers[e.chunk.pos] ?: return

            ChunkDataRegistry.getApplicable(e.chunk).forEach {
                val data = container.datas[it.clazz] ?: return@forEach
                val nbt = data.saveToNBT(NBTTagCompound())
                rootData.setTag(it.name.toString(), nbt)
            }

            e.data.setTag("librarianlib_chunkdata", rootData)
        }

        @SubscribeEvent
        fun unloadChunk(e: ChunkEvent.Unload) {
            get(e.chunk.world).containers.remove(e.chunk.pos)
        }

        @SubscribeEvent
        fun clientLoad(e: ChunkEvent.Load) {
            if (e.chunk.world.isRemote) {
                val container = ChunkDataContainer()
                ChunkDataRegistry.getApplicable(e.chunk).forEach {
                    val data = it.constructor(e.chunk)
                    data.name = it.name
                    container.datas[it.clazz] = data
                }

                get(e.chunk.world).containers[e.chunk.pos] = container
            } else {
                val worldData = get(e.chunk.world)
                if(!worldData.containers.contains(e.chunk.pos)) {
                    val container = ChunkDataContainer()
                    ChunkDataRegistry.getApplicable(e.chunk).forEach {
                        val data = it.constructor(e.chunk)
                        data.name = it.name
                        data.initializeNewChunk()
                        container.datas[it.clazz] = data
                    }

                    worldData.containers[e.chunk.pos] = container
                }
            }
        }

        @SubscribeEvent
        fun watch(e: ChunkWatchEvent.Watch) {
            val container = get(e.player.world).containers[e.chunk] ?: return

            container.datas.forEach {
                val dataType = ChunkDataRegistry.get(it.key) ?: return@forEach
                PacketHandler.NETWORK.sendTo(PacketCustomChunkData(e.chunk, dataType.name, it.value), e.player)
            }
        }

        fun get(world: World): ChunkWorldData {
            return world.perWorldStorage.getOrLoadData(ChunkWorldData::class.java, ChunkWorldData.NAME) as? ChunkWorldData ?:
                    ChunkWorldData().also { world.perWorldStorage.setData(ChunkWorldData.NAME, it) }
        }
    }

    // This data is never saved, only stored at runtime. Remove all ability to save to the world:
    override fun writeToNBT(compound: NBTTagCompound?) = compound // noop

    override fun readFromNBT(nbt: NBTTagCompound?) {} // noop
    override fun markDirty() {} // noop
    override fun isDirty(): Boolean {
        return false
    } // noop

    override fun setDirty(isDirty: Boolean) {} // noop
}

class ChunkDataContainer {
    val datas = mutableMapOf<Class<*>, ChunkData>()
}
