package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.network.PacketSynchronization
import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
abstract class TileMod : TileEntity() {

    /**
     * Using fast synchronization is quicker and less expensive than the NBT packet default.
     * However, it requires you to register both ByteBuf serializers and NBT serializers for custom types.
     * If you for some reason can only use NBT serializers, turn this property to false.
     */
    open val useFastSync: Boolean
        get() = true

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        writeCustomNBT(compound, false)
        AbstractSaveHandler.writeAutoNBT(this, compound)
        super.writeToNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        readCustomNBT(compound)
        AbstractSaveHandler.readAutoNBT(this, compound)
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        val compound = super.getUpdateTag()
        writeCustomNBT(compound, true)
        AbstractSaveHandler.writeAutoNBT(this, compound, true)
        super.writeToNBT(compound)

        return compound
    }

    override fun getUpdatePacket(): SPacketUpdateTileEntity {
        return SPacketUpdateTileEntity(pos, -999, updateTag)
    }

    /**
     * Override this function to store special data not stored in @Save fields in NBT.
     * If [useFastSync] is false, this will also determine whether it gets sent to clientside.
     *
     * [sync] implies that this is being used to send to clientside.
     */
    open fun writeCustomNBT(cmp: NBTTagCompound, sync: Boolean) {
        // NO-OP
    }

    /**
     * Override this function to read special data not stored in @Save fields from NBT.
     * If [useFastSync] is false, this will also determine what the client receives.
     */
    open fun readCustomNBT(cmp: NBTTagCompound) {
        // NO-OP
    }

    /**
     * Override this function to write special data not stored in @Save fields to bytes.
     * If [useFastSync] is false, this function is never called.
     *
     * [sync] implies that this is being used to send to clientside.
     */
    open fun writeCustomBytes(buf: ByteBuf, sync: Boolean) {
        // NO-OP
    }

    /**
     * Override this function to read special data not stored in @Save fields from bytes.
     * If [useFastSync] is false, this function is never called.
     */
    open fun readCustomBytes(buf: ByteBuf) {
        // NO-OP
    }

    override fun markDirty() {
        super.markDirty()
        if (!worldObj.isRemote)
            dispatchTileToNearbyPlayers()
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        readCustomNBT(packet.nbtCompound)
        AbstractSaveHandler.readAutoNBT(javaClass, packet.nbtCompound)
    }

    open fun dispatchTileToNearbyPlayers() {
        if (worldObj is WorldServer) {
            val ws: WorldServer = worldObj as WorldServer

            for (player in ws.playerEntities) {
                val playerMP = player as EntityPlayerMP
                if (playerMP.getDistanceSq(getPos()) < 64 * 64
                        && ws.playerChunkMap.isPlayerWatchingChunk(playerMP, pos.x shr 4, pos.z shr 4)) {
                    if (useFastSync)
                        PacketHandler.NETWORK.sendTo(PacketSynchronization(this), playerMP)
                    else
                        playerMP.connection.sendPacket(updatePacket)
                }
            }
        }
    }
}
