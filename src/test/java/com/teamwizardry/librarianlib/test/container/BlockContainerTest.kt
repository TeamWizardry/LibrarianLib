package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.TileModInventory
import com.teamwizardry.librarianlib.features.container.GuiHandler
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class BlockContainerTest : BlockModContainer("container", Material.ROCK) {
    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TEContainer()
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        GuiHandler.open(ContainerTest.NAME, playerIn, pos)
        return true
    }
}


@TileRegister("container")
class TEContainer : TileModInventory(27 + 9)
