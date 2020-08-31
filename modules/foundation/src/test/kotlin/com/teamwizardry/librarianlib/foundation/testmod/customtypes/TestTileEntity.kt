package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.testmod.ModTiles
import com.teamwizardry.librarianlib.foundation.testmod.logger
import net.minecraft.entity.Entity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SUpdateTileEntityPacket
import net.minecraft.particles.ParticleTypes
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.util.Constants

class TestTileEntity(): TileEntity(ModTiles.testTile.get()) {
    var totalFallDistance: Float = 0f
    var lastFallDistance: Float = 0f

    init {
        logger.info("Test tile created")
    }

    fun onFallenUpon(entity: Entity, fallDistance: Float) {
        val world = world ?: return

        totalFallDistance += fallDistance
        lastFallDistance = fallDistance
        markDirty()
        world.notifyBlockUpdate(pos, blockState, blockState, Constants.BlockFlags.BLOCK_UPDATE)

        (world as? ServerWorld)?.spawnParticle(
            ParticleTypes.POOF,
            entity.posX, entity.posY, entity.posZ,
            5,
            0.1, 0.0, 0.1,
            0.0
        )
    }

    override fun write(compound: CompoundNBT): CompoundNBT {
        super.write(compound)
        compound.putFloat("totalFallDistance", totalFallDistance)
        compound.putFloat("lastFallDistance", lastFallDistance)
        return compound
    }

    override fun read(compound: CompoundNBT) {
        super.read(compound)
        totalFallDistance = compound.getFloat("totalFallDistance")
        lastFallDistance = compound.getFloat("lastFallDistance")
    }

    override fun getUpdateTag(): CompoundNBT {
        val tag = super.getUpdateTag()
        tag.putFloat("totalFallDistance", totalFallDistance)
        tag.putFloat("lastFallDistance", lastFallDistance)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundNBT) {
        super.handleUpdateTag(tag)
        totalFallDistance = tag.getFloat("totalFallDistance")
        lastFallDistance = tag.getFloat("lastFallDistance")
    }

    override fun getUpdatePacket(): SUpdateTileEntityPacket? {
        val tag = CompoundNBT()
        tag.putFloat("totalFallDistance", totalFallDistance)
        tag.putFloat("lastFallDistance", lastFallDistance)
        return SUpdateTileEntityPacket(getPos(), -1, tag)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SUpdateTileEntityPacket) {
        val tag = pkt.nbtCompound
        totalFallDistance = tag.getFloat("totalFallDistance")
        lastFallDistance = tag.getFloat("lastFallDistance")
    }
}