package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.network.PacketHandler
import com.teamwizardry.librarianlib.common.network.PacketSynchronization
import com.teamwizardry.librarianlib.common.util.saving.AbstractSaveHandler
import com.teamwizardry.librarianlib.common.util.saving.SaveInPlace
import io.netty.buffer.ByteBuf
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability

/**
 * @author WireSegal
 * Created at 11:06 AM on 8/4/16.
 */
@SaveInPlace
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
        val customTag = NBTTagCompound()
        writeCustomNBT(customTag, false)
        compound.setTag("custom", customTag)
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, false))
        compound.setInteger("_v", 2)
        super.writeToNBT(compound)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        readCustomNBT(compound.getCompoundTag("custom"))
        if (!compound.hasKey("_v"))
            AbstractSaveHandler.readAutoNBT(this, compound, false)
        else {
            when (compound.getInteger("_v")) {
                2 -> AbstractSaveHandler.readAutoNBT(this, compound.getTag("auto"), false)
            }
        }
        super.readFromNBT(compound)
    }

    override fun getUpdateTag(): NBTTagCompound {
        val compound = super.getUpdateTag()
        val customTag = NBTTagCompound()
        writeCustomNBT(customTag, true)
        compound.setTag("custom", customTag)
        compound.setTag("auto", AbstractSaveHandler.writeAutoNBT(this, true))
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

    /**
     * Tell the server and nearby clients that this tile has changed.
     */
    override fun markDirty() {
        super.markDirty()
        if (!world.isRemote)
            dispatchTileToNearbyPlayers()
    }

    override fun onDataPacket(net: NetworkManager, packet: SPacketUpdateTileEntity) {
        super.onDataPacket(net, packet)
        handleUpdateTag(packet.nbtCompound)
    }

    override fun handleUpdateTag(tag: NBTTagCompound) {
        readCustomNBT(tag.getCompoundTag("custom"))
        AbstractSaveHandler.readAutoNBT(this, tag.getTag("auto"), true)
    }

    /**
     * Dispatch tile data to nearby players. This will sync data to client side.
     */
    open fun dispatchTileToNearbyPlayers() {
        if (world is WorldServer) {
            val ws: WorldServer = world as WorldServer

            ws.playerEntities
                    .filterIsInstance<EntityPlayerMP>()
                    .filter { it.getDistanceSq(getPos()) < 64 * 64 && ws.playerChunkMap.isPlayerWatchingChunk(it, pos.x shr 4, pos.z shr 4) }
                    .forEach { sendUpdatePacket(it) }
        }
    }

    /**
     * The specific implementation for an update packet.
     * By default, controlled by [useFastSync], it will send the vanilla packet or the LibLib sync packet.
     */
    open fun sendUpdatePacket(player: EntityPlayerMP) {
        if (useFastSync)
            PacketHandler.NETWORK.sendTo(PacketSynchronization(this), player)
        else
            player.connection.sendPacket(updatePacket)
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return AbstractSaveHandler.getCapability(this, capability, facing) ?: super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return AbstractSaveHandler.hasCapability(this, capability, facing) || super.hasCapability(capability, facing)
    }
}
