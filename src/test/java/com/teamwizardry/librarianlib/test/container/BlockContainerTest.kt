package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.features.base.block.BlockMod
import com.teamwizardry.librarianlib.features.base.block.TileModInventory
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import net.minecraft.block.ITileEntityProvider
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
class BlockContainerTest : BlockMod("container", Material.ROCK), ITileEntityProvider {
    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        GuiHandler.open(ContainerTest.NAME, playerIn, pos)
        return true
    }


    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        return TEContainer()
    }
}


@TileRegister("container")
class TEContainer : TileModInventory(27 + 9)
