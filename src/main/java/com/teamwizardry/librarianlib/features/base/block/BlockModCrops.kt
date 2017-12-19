package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.IGrowable
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
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

    constructor(name: String) : this(name, 7)

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


        private var lastStages by threadLocal { -1 }

        private fun injectStages(name: String, stages: Int): String {
            lastStages = MathHelper.clamp(stages, 0, 7)
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
            property = PropertyInteger.create("age", 0, lastStages)
            createdProperty = true
        }
        return property
    }

    open fun getMaxAge(): Int = getAgeProperty().allowedValues.size

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
        return MathHelper.getInt(worldIn.rand, getMaxAge() / 2 - 1, getMaxAge() / 2 + 1)
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

    abstract fun getSeed(): Item

    abstract fun getCrop(): Item

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        super.getDrops(drops, world, pos, state, 0)
        val age = getAge(state)
        val rand = if (world is World) world.rand else Random()

        if (age >= getMaxAge()) {
            var any = false
            for (i in 0..3 + fortune - 1) if (rand.nextInt(2 * getMaxAge()) <= age) {
                drops.add(ItemStack(this.getSeed(), 1, 0))
                any = true
            }
            if (!any)
                drops.add(ItemStack(this.getSeed(), 1, 0))
        }
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int)
            = if (this.isMaxAge(state)) this.getCrop() else this.getSeed()

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack
            = ItemStack(getSeed())

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

    override fun createItemForm() = null

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                json { obj("model" to registryName.toString() + ("age=(\\d+)".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0)) }
            }
        }, {
            (0 until getMaxAge()).associate {
                JsonGenerationUtils.getPathForBlockModel(this, registryName.toString() + it) to json {
                    obj(
                            "parent" to "block/crop",
                            "textures" to obj(
                                    "all" to "${registryName!!.resourceDomain}:blocks/${registryName!!.resourcePath}$it"
                            )
                    )
                }
            }
        })
        return true
    }
}
