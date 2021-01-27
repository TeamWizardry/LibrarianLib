package com.teamwizardry.librarianlib.foundation.testmod.customtypes

import com.teamwizardry.librarianlib.foundation.block.BaseBlock
import com.teamwizardry.librarianlib.foundation.block.FoundationBlockProperties
import com.teamwizardry.librarianlib.foundation.registration.LazyTileEntityType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.common.extensions.IForgeBlock

class TestTileBlock(properties: FoundationBlockProperties): BaseBlock(properties), IForgeBlock {
    override fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): ActionResultType {
        val prefix = if(worldIn.isRemote) "[Client]" else "[Server]"
        val tile = worldIn.getTileEntity(pos) as? TestTileEntity ?: return ActionResultType.CONSUME
        player.sendStatusMessage(StringTextComponent("$prefix totalFallDistance: ${tile.totalFallDistance} lastFallDistance: ${tile.lastFallDistance}"), false)

        return ActionResultType.CONSUME
    }

    override fun onFallenUpon(worldIn: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance)
        if(!worldIn.isRemote) {
            val tile = worldIn.getTileEntity(pos) as? TestTileEntity ?: return
            tile.onFallenUpon(entityIn, fallDistance)
        }
    }

    override fun hasTileEntity(state: BlockState?): Boolean {
        return true
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return TestTileEntity()
    }

    override fun isTransparent(state: BlockState): Boolean {
        return true
    }
}