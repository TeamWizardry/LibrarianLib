package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 10:36 AM on 5/7/16.
 */
@Suppress("LeakingThis")
open class BlockModDirectional(name: String, material: Material, vararg directions: EnumFacing) : BlockMod(name, injectDirections(material, directions)), IModelGenerator {
    companion object {
        private var lastDirections: Array<out EnumFacing> by threadLocal {
            arrayOf<EnumFacing>()
        }

        /**
         * Hacky nonsense required because constructor and associated arguments
         * aren't available until super's construction is complete.
         *
         * This captures the directions during construction and injects them into the [property]
         * created by first access in [createBlockState].
         */
        private fun injectDirections(material: Material, directions: Array<out EnumFacing>): Material {
            lastDirections = if (directions.isEmpty()) EnumFacing.VALUES else directions
            return material
        }
    }

    lateinit var directions: Array<out EnumFacing>
        private set

    lateinit var property: PropertyDirection
        private set

    override fun createBlockState(): BlockStateContainer {
        directions = lastDirections
        property = PropertyDirection.create("facing", directions.toList())
        return BlockStateContainer(this, property)
    }

    override fun getStateForPlacement(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        return this.getStateFromMeta(meta).withProperty(property, facing)
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> {
                state.withProperty(property, rot.rotate(state[property]))
            }

            else -> state
        }
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing?): Boolean {
        val state = world.getBlockState(pos)
        world.setBlockState(pos, state.withProperty(property, axis))
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val prop = directions[meta % directions.size]

        return this.defaultState.withProperty(property, prop)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return directions.indexOf(state[property])
    }

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                val facing = "${property.name}=(\\w+)".toRegex().find(it)?.groupValues?.get(1)?.toUpperCase()
                val dir = EnumFacing.byName(facing)
                val x = if (dir == EnumFacing.DOWN) 180 else if (dir == EnumFacing.SOUTH) 270 else if (dir == EnumFacing.UP) 0 else 90
                val y = if (dir == EnumFacing.EAST) 90 else if (dir == EnumFacing.WEST) 270 else 0
                json {
                    obj(
                            "model" to registryName.toString(),
                            *if (x != 0) arrayOf("x" to x) else arrayOf(),
                            *if (y != 0) arrayOf("y" to y) else arrayOf()
                    )
                }
            }
        }, {
            mapOf(JsonGenerationUtils.getPathForBlockModel(this)
                    to JsonGenerationUtils.generateBaseBlockModel(this))
        })
        return true
    }
}
