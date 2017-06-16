package com.teamwizardry.librarianlib.features.base.block

import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 5:45 PM on 3/20/16.
 */
abstract class BlockModContainer(name: String, materialIn: Material, vararg variants: String) : BlockMod(name, materialIn, *variants) {

    override fun eventReceived(state: IBlockState?, worldIn: World, pos: BlockPos, eventID: Int, eventParam: Int): Boolean {
        val tile = worldIn.getTileEntity(pos) ?: return false
        return tile.receiveClientEvent(eventID, eventParam)
    }

    override fun hasTileEntity(state: IBlockState?) = true

    override abstract fun createTileEntity(world: World, state: IBlockState): TileEntity?

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos)
        if (tile is TileMod) tile.onBreak()
        super.breakBlock(worldIn, pos, state)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = worldIn.getTileEntity(pos)
        return tile is TileMod && tile.onClicked(playerIn, hand, facing, hitX, hitY, hitZ)
    }
}
