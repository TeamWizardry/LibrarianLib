package com.teamwizardry.librarianlib.test.bitstorage.block

import com.teamwizardry.librarianlib.test.testcore.block.BlockTestingBase
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Created by TheCodeWarrior
 */
class BlockIntSaveTest : BlockTestingBase(Material.ROCK, "intSaveTest") {
    init { TileIntSaveTest }
    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity {
        return TileIntSaveTest()
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, arrayOf(), arrayOf(index, array_0, array_1, array_2, array_3, map_north, map_south, map_east, map_west, map_up, map_down))
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile: TileIntSaveTest = world.getTileEntity(pos) as TileIntSaveTest
        return (state as IExtendedBlockState)
                .withProperty(index, tile.arrayIndex)
                .withProperty(array_0, tile.array[0])
                .withProperty(array_1, tile.array[1])
                .withProperty(array_2, tile.array[2])
                .withProperty(array_3, tile.array[3])
                .withProperty(map_north, tile.sides[EnumFacing.NORTH])
                .withProperty(map_south, tile.sides[EnumFacing.SOUTH])
                .withProperty(map_east, tile.sides[EnumFacing.EAST])
                .withProperty(map_west, tile.sides[EnumFacing.WEST])
                .withProperty(map_up, tile.sides[EnumFacing.UP])
                .withProperty(map_down, tile.sides[EnumFacing.DOWN])
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile: TileIntSaveTest = worldIn.getTileEntity(pos) as TileIntSaveTest


        if(player.isSneaking && side == EnumFacing.UP) {
            tile.arrayIndex++
        }

        tile.array[tile.arrayIndex] += if(player.isSneaking) -1 else 1
        tile.sides[side] += if(player.isSneaking) -1 else 1

        return true
    }

    companion object {
        val index = UPropertyInt("index")

        val array_0 = UPropertyInt("array_0")
        val array_1 = UPropertyInt("array_1")
        val array_2 = UPropertyInt("array_2")
        val array_3 = UPropertyInt("array_3")

        val map_north = UPropertyInt("map_n")
        val map_south = UPropertyInt("map_s")
        val map_east = UPropertyInt("map_e")
        val map_west = UPropertyInt("map_w")
        val map_up = UPropertyInt("map_u")
        val map_down = UPropertyInt("map_d")
    }
}
