package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.common.LibLibConfig
import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenTrees
import net.minecraftforge.event.terraingen.TerrainGen
import java.util.*

/**
 * @author WireSegal
 * Created at 10:13 PM on 5/27/16.
 */
@Suppress("LeakingThis")
abstract class BlockModSapling(name: String, vararg variants: String) : BlockModBush(name, *variants), IGrowable {

    companion object {

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

    override fun getBurnTime(stack: ItemStack): Int {
        return 100
    }

    init {
        this.tickRandomly = true
        if (itemForm != null)
            for (variant in this.variants.indices)
                OreDictionaryRegistrar.registerOre("treeSapling") { ItemStack(itemForm, 1, variant) }
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

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, STAGE)
    }
}

