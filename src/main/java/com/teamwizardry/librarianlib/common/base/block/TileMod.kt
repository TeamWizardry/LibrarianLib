package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.core.LibLibConfig
import com.teamwizardry.librarianlib.common.util.saving.TileFieldCache
import com.teamwizardry.librarianlib.common.util.saving.TileSerializationHandlers
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileMod : TileEntity() {

    companion object {
        @JvmStatic
        fun registerTile(clazz: Class<out TileMod>, id: String) {
            TileFieldCache.getClassFields(clazz)
            GameRegistry.registerTileEntity(clazz, id)
        }
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        writeCustomNBT(compound)
        writeAutoNBT(compound)
        super.writeToNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        readCustomNBT(compound)
        readAutoNBT(compound)
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        return writeToNBT(super.getUpdateTag())
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, -999, updateTag)
    }

    open fun writeCustomNBT(cmp: NBTTagCompound) {
        // NO-OP
    }

    open fun readCustomNBT(cmp: NBTTagCompound) {
        // NO-OP
    }

    fun writeAutoNBT(cmp: NBTTagCompound) {
        if (LibLibConfig.autoSaveTEs) {
            TileFieldCache.getClassFields(javaClass).forEach {
                val handler = TileSerializationHandlers.getWriterUnchecked(it.value.first)
                if (handler != null) {
                    val value = it.value.second(this)
                    if (value != null) cmp.setTag(it.key, handler(value))
                }
            }
        }
    }

    fun readAutoNBT(cmp: NBTTagCompound) {
        if (LibLibConfig.autoSaveTEs) {
            TileFieldCache.getClassFields(javaClass).forEach {
                val handler = TileSerializationHandlers.getReaderUnchecked(it.value.first)
                if (handler != null)
                    it.value.third(this, handler(cmp.getTag(it.key)))
            }
        }
    }

    override fun markDirty() {
        super.markDirty()
        if (!worldObj.isRemote)
            dispatchTileToNearbyPlayers()
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        readCustomNBT(packet.nbtCompound)
        readAutoNBT(packet.nbtCompound)
    }

    open fun dispatchTileToNearbyPlayers() {
        if (worldObj is WorldServer) {
            val ws: WorldServer = worldObj as WorldServer
            val packet: SPacketUpdateTileEntity = updatePacket

            for (player in ws.playerEntities) {
                val playerMP = player as EntityPlayerMP
                if (playerMP.getDistanceSq(getPos()) < 64 * 64
                        && ws.playerChunkMap.isPlayerWatchingChunk(playerMP, pos.x shr 4, pos.z shr 4)) {
                    playerMP.connection.sendPacket(packet)
                }
            }
        }
    }
}
