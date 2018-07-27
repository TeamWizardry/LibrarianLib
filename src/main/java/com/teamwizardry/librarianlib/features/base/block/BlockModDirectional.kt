package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.helpers.threadLocal
import com.teamwizardry.librarianlib.features.kotlin.extract
import com.teamwizardry.librarianlib.features.kotlin.get
import com.teamwizardry.librarianlib.features.utilities.generateBaseBlockModel
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
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
open class BlockModDirectional(name: String, material: Material, horizontal: Boolean = true, vararg variants: String) : BlockMod(name, injectDirections(material, horizontal), *variants), IModelGenerator {
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
    override fun getStateForPlacement(worldIn: World?, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
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

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        if (!isHorizontal || axis.horizontalIndex >= 0)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(property, axis))
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

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                "model" to key

                val dir = EnumFacing.byName(it.extract("${property.name}=(\\w+)", default = "UP"))
                        ?: EnumFacing.DOWN

                if (dir == EnumFacing.DOWN)
                    "x" to 270
                else if (dir == EnumFacing.UP)
                    "x" to 90

                if (dir.horizontalIndex != -1 && dir.horizontalAngle != 0f)
                    "y" to (dir.horizontalAngle.toInt() - 180) % 360

            }
        }, {
            getPathForBlockModel(this) to generateBaseBlockModel(this)
        })

        return true
    }
}
