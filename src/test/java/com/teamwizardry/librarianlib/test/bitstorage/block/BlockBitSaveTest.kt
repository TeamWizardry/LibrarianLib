package com.teamwizardry.librarianlib.test.bitstorage.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.PropertyFloat

/**
 * Created by TheCodeWarrior
 */
class BlockBitSaveTest : Block(Material.ROCK) {
    override fun hasTileEntity(state: IBlockState?): Boolean {
        return true
    }

    override fun createTileEntity(world: World?, state: IBlockState?): TileEntity {
        return TileBitSaveTest()
    }

    override fun createBlockState(): BlockStateContainer {
        return super.createBlockState()
    }

    override fun getActualState(state: IBlockState?, worldIn: IBlockAccess?, pos: BlockPos?): IBlockState {
        return super.getActualState(state, worldIn, pos)
    }

    companion object {
        //region int props
        val prop_arrayIndex = PropertyInteger.create("arrayIndex", 0, 3)
        
        val prop_arrayValue0 = PropertyInteger.create("arrayValue_0", 0, 7)
        val prop_arrayValue1 = PropertyInteger.create("arrayValue_1", 0, 7)
        val prop_arrayValue2 = PropertyInteger.create("arrayValue_2", 0, 7)
        val prop_arrayValue3 = PropertyInteger.create("arrayValue_3", 0, 7)

        val prop_sideValue_north = PropertyInteger.create("sideValue_north", 0, 7)
        val prop_sideValue_south = PropertyInteger.create("sideValue_south", 0, 7)
        val prop_sideValue_east = PropertyInteger.create("sideValue_east", 0, 7)
        val prop_sideValue_west = PropertyInteger.create("sideValue_west", 0, 7)
        val prop_sideValue_up = PropertyInteger.create("sideValue_up", 0, 7)
        val prop_sideValue_down = PropertyInteger.create("sideValue_down", 0, 7)
        //endregion

        //region float props
        val prop_hitVal = PropertyFloat("hitVal")

        val prop_hitX = PropertyFloat("hitX")
        val prop_hitY = PropertyFloat("hitY")
        val prop_hitZ = PropertyFloat("hitZ")

//        val prop_sideValue_north = PropertyFloat("sideValue_north")
//        val prop_sideValue_south = PropertyFloat("sideValue_south")
//        val prop_sideValue_east = PropertyFloat("sideValue_east")
//        val prop_sideValue_west = PropertyFloat("sideValue_west")
//        val prop_sideValue_up = PropertyFloat("sideValue_up")
//        val prop_sideValue_down = PropertyFloat("sideValue_down")
        //endregion

    }
}
