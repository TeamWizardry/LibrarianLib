package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.common.base.IModelGenerator
import net.minecraft.block.BlockLog
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

/**
 * @author WireSegal
 * Created at 10:36 AM on 5/7/16.
 */
@Suppress("LeakingThis")
open class BlockModLog(name: String, vararg variants: String) : BlockMod(name, Material.WOOD, *variants), IModelGenerator {
    companion object {
        val AXIS: PropertyEnum<BlockLog.EnumAxis> = PropertyEnum.create("axis", BlockLog.EnumAxis::class.java)
    }

    init {
        blockHardness = 2.0f
        soundType = SoundType.WOOD
        if (itemForm != null) {
            for (variant in this.variants.indices)
                OreDictionary.registerOre("logWood", ItemStack(this, 1, variant))
            FurnaceRecipes.instance().addSmeltingRecipeForBlock(this, ItemStack(Items.COAL, 1, 1), 0.15f)
        }
        defaultState = defaultState.withProperty(AXIS, BlockLog.EnumAxis.Y)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i = 4
        val j = i + 1

        if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
            for (blockpos in BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                val iblockstate = worldIn.getBlockState(blockpos)

                if (iblockstate.block.isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.block.beginLeavesDecay(iblockstate, worldIn, blockpos)
                }
            }
        }
    }

    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5

    override fun getStateForPlacement(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        return this.getStateFromMeta(meta).withProperty(AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.axis))
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> {

                when (state.getValue(AXIS)) {
                    BlockLog.EnumAxis.X -> return state.withProperty(AXIS, BlockLog.EnumAxis.Z)
                    BlockLog.EnumAxis.Z -> return state.withProperty(AXIS, BlockLog.EnumAxis.X)
                    else -> return state
                }
            }

            else -> return state
        }
    }

    override fun canSustainLeaves(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun isWood(world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing?): Boolean {
        val state = world.getBlockState(pos)
        world.setBlockState(pos, state.cycleProperty(AXIS))
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var axis = BlockLog.EnumAxis.Y
        val i = meta and 12

        when (i) {
            4 -> axis = BlockLog.EnumAxis.X
            8 -> axis = BlockLog.EnumAxis.Z
            12 -> axis = BlockLog.EnumAxis.NONE
        }

        return this.defaultState.withProperty(AXIS, axis)
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        state ?: return 0
        var i = 0

        when (state.getValue(AXIS)) {
            BlockLog.EnumAxis.X -> i = i or 4
            BlockLog.EnumAxis.Z -> i = i or 8
            BlockLog.EnumAxis.NONE -> i = i or 12
            else -> i = i or 0
        }

        return i
    }

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, AXIS)
    }

    override fun getHarvestTool(state: IBlockState?): String? {
        return "axe"
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return type == "axe"
    }

    //todo model generation
}
