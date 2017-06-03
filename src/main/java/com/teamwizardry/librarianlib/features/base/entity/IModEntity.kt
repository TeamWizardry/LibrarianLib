package com.teamwizardry.librarianlib.features.base.entity

import com.teamwizardry.librarianlib.features.network.PacketEntitySynchronization
import com.teamwizardry.librarianlib.features.network.PacketHandler
import com.teamwizardry.librarianlib.features.saving.Savable
import io.netty.buffer.ByteBuf
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.WorldServer

/**
 * @author WireSegal
 * Created at 4:18 PM on 5/23/17.
 */
@Savable
interface IModEntity {

    fun dispatchEntityToNearbyPlayers() {
        val world = (this as Entity).world
        if (world is WorldServer) {

            val pos = position

            world.playerEntities
                    .filterIsInstance<EntityPlayerMP>()
                    .filter { it.getDistanceSq(pos) < 64 * 64 && world.playerChunkMap.isPlayerWatchingChunk(it, pos.x shr 4, pos.z shr 4) }
                    .forEach { sendUpdatePacket(it) }
        }
    }

    /**
     * The specific implementation for an update packet.
     */
    fun sendUpdatePacket(player: EntityPlayerMP) {
        PacketHandler.NETWORK.sendTo(PacketEntitySynchronization((this as Entity).entityId, this), player)
    }

    fun writeCustomBytes(buf: ByteBuf) {
        // NO-OP
    }

    fun readCustomBytes(buf: ByteBuf) {
        // NO-OP
    }

    fun writeCustomNBT(compound: NBTTagCompound) {
        // NO-OP
    }

    fun readCustomNBT(compound: NBTTagCompound) {
        // NO-OP
    }
}
