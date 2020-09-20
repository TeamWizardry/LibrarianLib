package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.testmod.ModTiles
import com.teamwizardry.librarianlib.foundation.testmod.logger
import com.teamwizardry.librarianlib.foundation.tileentity.BaseTileEntity
import com.teamwizardry.librarianlib.prism.Save
import com.teamwizardry.librarianlib.prism.Sync
import net.minecraft.entity.Entity
import net.minecraft.particles.ParticleTypes
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.util.Constants

class TestTileEntity: BaseTileEntity(ModTiles.testTile.get()) {
    @Save
    var totalFallDistance: Float = 0f
    @Sync
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
}