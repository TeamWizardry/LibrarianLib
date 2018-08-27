package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.extract
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.IPlantable
import java.util.*

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
abstract class BlockModCrops(name: String, stages: Int) : BlockModBush(injectStages(name, stages)), IGrowable {

    constructor(name: String) : this(name, 8)

    companion object {
        val CROPS_AABB = arrayOf(
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))


        private var lastStages by threadLocal { 8 }

        private fun injectStages(name: String, stages: Int): String {
            lastStages = MathHelper.clamp(stages, 1, 8)
            return name
        }
    }

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos)
            = CROPS_AABB[state.getValue(this.getAgeProperty()) * getAgeProperty().allowedValues.size / CROPS_AABB.size]

    /**
     * Return true if the block can sustain a Bush
     */
    override fun canSustainBush(state: IBlockState): Boolean = state.block == Blocks.FARMLAND


    private var createdProperty = false
    private lateinit var property: PropertyInteger
    open fun getAgeProperty(): PropertyInteger {
        if (!createdProperty) {
            property = PropertyInteger.create("age", 0, lastStages - 1)
            createdProperty = true
        }
        return property
    }

    open fun getMaxAge(): Int = getAgeProperty().allowedValues.size - 1

    open fun getAge(state: IBlockState): Int = state.getValue(this.getAgeProperty())

    open fun withAge(age: Int): IBlockState = this.defaultState.withProperty(this.getAgeProperty(), age)

    open fun isMaxAge(state: IBlockState): Boolean = state.getValue(this.getAgeProperty()) >= this.getMaxAge()

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        super.updateTick(worldIn, pos, state, rand)

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
            val age = this.getAge(state)

            if (age < this.getMaxAge()) {
                val chance = getGrowthChance(this, worldIn, pos)

                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((25.0f / chance).toInt() + 1) == 0)) {
                    worldIn.setBlockState(pos, this.withAge(age + 1), 2)
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos))
                }
            }
        }
    }

    open fun getBonemealAgeIncrease(worldIn: World): Int {
        return MathHelper.getInt(worldIn.rand, Math.max(getMaxAge() / 2 - 1, 1), getMaxAge() / 2 + 1)
    }

    open fun getGrowthChance(blockIn: Block, worldIn: World, pos: BlockPos): Float {
        var chance = 1.0f
        val soil = pos.down()

        for (x in -1..1) {
            for (z in -1..1) {
                var chanceModifier = 0.0f
                val stateAt = worldIn.getBlockState(soil.add(x, 0, z))

                if (stateAt.block.canSustainPlant(stateAt, worldIn, soil.add(x, 0, z), net.minecraft.util.EnumFacing.UP, blockIn as IPlantable)) {
                    chanceModifier = 1.0f

                    if (stateAt.block.isFertile(worldIn, soil.add(x, 0, z))) {
                        chanceModifier = 3.0f
                    }
                }

                if (x != 0 || z != 0) {
                    chanceModifier /= 4.0f
                }

                chance += chanceModifier
            }
        }

        val north = pos.north()
        val south = pos.south()
        val west = pos.west()
        val east = pos.east()
        val westEastBlocked = blockIn == worldIn.getBlockState(west).block || blockIn == worldIn.getBlockState(east).block
        val northSouthBlocked = blockIn == worldIn.getBlockState(north).block || blockIn == worldIn.getBlockState(south).block

        if (westEastBlocked && northSouthBlocked) chance /= 2.0f
        else if (blockIn == worldIn.getBlockState(west.north()).block ||
                blockIn == worldIn.getBlockState(east.north()).block ||
                blockIn == worldIn.getBlockState(east.south()).block ||
                blockIn == worldIn.getBlockState(west.south()).block) chance /= 2.0f

        return chance
    }

    override fun canBlockStay(worldIn: World, pos: BlockPos, state: IBlockState): Boolean {
        val soil = worldIn.getBlockState(pos.down())
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && soil.block.canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this)
    }

    abstract fun getSeed(): ItemStack

    abstract fun getDefaultCrop(): ItemStack

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val rand = if (world is World) world.rand else Random()

        val count = quantityDropped(state, fortune, rand)
        for (i in 0 until count)
            drops.add(getSeed())

        if (shouldDrop(world, pos, state, fortune) && !provideDrops(drops, world, pos, state, rand, fortune))
            drops.add(getSeed())
    }

    open fun provideDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, random: Random, fortune: Int): Boolean {
        val age = getAge(state)
        val size = drops.size

        for (i in 0 until dropCount(world, pos, state, fortune))
            if (random.nextInt(2 * getMaxAge()) <= age)
                drops.add(generateDrop(world, pos, state, fortune, i))

        return size < drops.size
    }

    open fun shouldDrop(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) = getAge(state) >= getMaxAge()
    open fun dropCount(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) = 3 + fortune
    open fun generateDrop(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int, dropped: Int) = getDefaultCrop()

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack
            = if (getAge(state) >= getMaxAge()) getDefaultCrop() else getSeed()

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean = !this.isMaxAge(state)

    override fun canUseBonemeal(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState): Boolean = true

    override fun grow(worldIn: World, rand: Random, pos: BlockPos, state: IBlockState) {
        var newAge = this.getAge(state) + this.getBonemealAgeIncrease(worldIn)
        val max = this.getMaxAge()
        if (newAge > max) newAge = max
        worldIn.setBlockState(pos, this.withAge(newAge), 2)
    }

    override fun getStateFromMeta(meta: Int): IBlockState = this.withAge(meta)

    override fun getMetaFromState(state: IBlockState): Int = this.getAge(state)

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, getAgeProperty())

    override fun createItemForm(): ItemBlock? = null

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                val age = it.extract("age=(\\d+)").toIntOrNull() ?: 0
                "model"("$key$age")
            }
        }, {
            for (i in 0 until getMaxAge())
                getPathForBlockModel(this, "$key$i") to {
                    "parent"("block/crop")
                    "textures" {
                        "all"("${key.namespace}:blocks/${key.path}$i")
                    }
                }
        })
        return true
    }
}
