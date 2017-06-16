package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenTrees
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.IPlantable
import net.minecraftforge.event.terraingen.TerrainGen
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import java.util.*

/**
 * @author WireSegal
 * Created at 10:13 PM on 5/27/16.
 */
@Suppress("LeakingThis")
abstract class BlockModSapling(name: String, vararg variants: String) : BlockModBush(name, *variants), IGrowable {

    companion object : IFuelHandler {
        override fun getBurnTime(fuel: ItemStack)
                = if (fuel.item is ItemBlock && (fuel.item as ItemBlock).block is BlockModSapling) 100 else 0

        init {
            GameRegistry.registerFuelHandler(this)
        }

        val STAGE: PropertyInteger = PropertyInteger.create("stage", 0, 1)

        fun defaultSaplingBehavior(world: World, pos: BlockPos, state: IBlockState, rand: Random, wood: IBlockState, leaves: IBlockState) {
            if (!TerrainGen.saplingGrowTree(world, rand, pos)) return

            world.setBlockState(pos, Blocks.AIR.defaultState, 4)

            if (!WorldGenTrees(true, 4, wood, leaves, false).generate(world, rand, pos))
                world.setBlockState(pos, state, 4)
        }

        fun defaultSaplingBehavior(world: World, pos: BlockPos, state: IBlockState, rand: Random, wood: Block, leaves: Block) {
            defaultSaplingBehavior(world, pos, state, rand, wood.defaultState, leaves.defaultState)
        }
    }

    init {
        this.tickRandomly = true
        if (itemForm != null)
            for (variant in this.variants.indices)
                OreDictionary.registerOre("treeSapling", ItemStack(this, 1, variant))
    }

    override fun onNeighborChange(worldIn: IBlockAccess, pos: BlockPos, neighborBlock: BlockPos) {
        super.onNeighborChange(worldIn, pos, neighborBlock)
        if (worldIn is World)
            this.checkAndDropBlock(worldIn, pos)
    }

    fun checkAndDropBlock(worldIn: World, pos: BlockPos) {
        val state = worldIn.getBlockState(pos)
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0)
            worldIn.setBlockState(pos, Blocks.AIR.defaultState, 3)
        }
    }

    override val ignoredProperties: Array<IProperty<*>>?
        get() = arrayOf(STAGE)

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        return true
    }

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean {
        return worldIn.rand.nextFloat().toDouble() < 0.45 || LibLibConfig.oneBonemeal
    }

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        this.grow(worldIn, pos, state, rand)
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            checkAndDropBlock(worldIn, pos)

            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
                this.grow(worldIn, pos, state, rand)
        }
    }

    fun grow(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!LibLibConfig.oneBonemeal && (state.getValue(STAGE) as Int).toInt() == 0)
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4)
        else this.generateTree(worldIn, pos, state, rand)
    }

    abstract fun generateTree(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random)

    override fun getStateFromMeta(meta: Int): IBlockState? {
        return defaultState.withProperty(STAGE, meta and 1)
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        return (state ?: return 0).getValue(STAGE)
    }

    override fun getPlant(world: IBlockAccess, pos: BlockPos): IBlockState {
        val state = world.getBlockState(pos)
        if (state.block !== this) return defaultState
        return state
    }

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, STAGE)
    }
}

