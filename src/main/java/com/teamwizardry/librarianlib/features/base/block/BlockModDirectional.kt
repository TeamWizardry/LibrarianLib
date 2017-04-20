package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.JSON.obj
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockHorizontal
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
open class BlockModDirectional(name: String, material: Material, horizontal: Boolean) : BlockMod(name, injectDirections(material, horizontal)), IModelGenerator {
    companion object {
        private var lastHorizontalState: Boolean by threadLocal { false }

        /**
         * Hacky nonsense required because constructor and associated arguments
         * aren't available until super's construction is complete.
         *
         * This captures the directions during construction and injects them into the [property]
         * created by first access in [createBlockState].
         */
        private fun injectDirections(material: Material, directions: Boolean): Material {
            lastHorizontalState = directions
            return material
        }
    }

    var isHorizontal: Boolean = false
        private set

    lateinit var property: PropertyDirection
        private set

    override fun createBlockState(): BlockStateContainer {
        isHorizontal = lastHorizontalState
        property = PropertyDirection.create("facing") {
            !isHorizontal || it?.horizontalIndex != -1
        }

        return BlockStateContainer(this, property)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getStateForPlacement(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return this.getStateFromMeta(meta).withProperty(property, if (isHorizontal) placer.horizontalFacing.opposite else facing)
    }

    @Suppress("OverridingDeprecatedMember")
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

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState {
        val prop = if (isHorizontal) EnumFacing.getHorizontal(meta and 0b11) else EnumFacing.getFront(meta and 0b111)

        return this.defaultState.withProperty(property, prop)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return if (isHorizontal) state.getValue(property).horizontalIndex else state.getValue(property).ordinal
    }

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                val facing = "${property.name}=(\\w+)".toRegex().find(it)?.groupValues?.get(1)?.toUpperCase()
                val dir = EnumFacing.byName(facing) ?: EnumFacing.DOWN
                val x = when (dir) {
                    EnumFacing.DOWN -> 270
                    EnumFacing.UP -> 90
                    else -> 0
                }
                val y = if (dir.horizontalIndex != -1) (dir.horizontalAngle.toInt() - 180) % 360 else 0

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
