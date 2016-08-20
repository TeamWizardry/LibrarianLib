package com.teamwizardry.librarianlib.bloat

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Teleporter
import net.minecraft.world.WorldServer

/**
 * Class provided by MCJty
 */
object TeleportUtil {

    fun teleportToDimension(player: EntityPlayer, dimension: Int, x: Double, y: Double, z: Double) {
        if (player !is EntityPlayerMP) return
        val oldDimension = player.worldObj.provider.dimension
        val server = player.worldObj.minecraftServer!!
        val worldServer = server.worldServerForDimension(dimension)
        player.addExperienceLevel(0)

        server.playerList.transferPlayerToDimension(player, dimension, CustomTeleporter(worldServer, x, y, z))
        player.setPositionAndUpdate(x, y, z)
        if (oldDimension == 1) {
            // For some reason teleporting out of the end does weird things.
            player.setPositionAndUpdate(x, y, z)
            worldServer.spawnEntityInWorld(player)
            worldServer.updateEntityWithOptionalForce(player, false)
        }
    }


    class CustomTeleporter(private val worldServer: WorldServer, private val x: Double, private val y: Double, private val z: Double) : Teleporter(worldServer) {

        override fun placeInPortal(entity: Entity, rotationYaw: Float) {
            this.worldServer.getBlockState(BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt()))

            entity.setPosition(this.x, this.y, this.z)
            entity.motionX = 0.0
            entity.motionY = 0.0
            entity.motionZ = 0.0
        }
    }
}
