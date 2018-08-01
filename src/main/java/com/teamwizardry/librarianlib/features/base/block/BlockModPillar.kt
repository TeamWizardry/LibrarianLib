package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.kotlin.extract
import com.teamwizardry.librarianlib.features.utilities.generateBaseBlockModel
import com.teamwizardry.librarianlib.features.utilities.generateBlockStates
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import net.minecraft.block.Block
import net.minecraft.block.BlockLog
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
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
open class BlockModPillar(name: String, material: Material, vararg variants: String) : BlockMod(name, material, *variants), IModelGenerator {
    companion object {
        val AXIS: PropertyEnum<BlockLog.EnumAxis> = PropertyEnum.create("axis", BlockLog.EnumAxis::class.java)
    }

    init {
        defaultState = defaultState.withProperty(AXIS, BlockLog.EnumAxis.Y)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        return this.getStateFromMeta(meta).withProperty(AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.axis))
    }

    @Suppress("OverridingDeprecatedMember")
    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> when (state.getValue(AXIS)) {
                BlockLog.EnumAxis.X -> state.withProperty(AXIS, BlockLog.EnumAxis.Z)
                BlockLog.EnumAxis.Z -> state.withProperty(AXIS, BlockLog.EnumAxis.X)
                else -> state
            }

            else -> state
        }
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing?): Boolean {
        val state = world.getBlockState(pos)
        world.setBlockState(pos, state.cycleProperty(AXIS))
        return true
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getStateFromMeta(meta: Int): IBlockState {
        val index = (meta and 0b1100) shl 2
        return defaultState.withProperty(AXIS, BlockLog.EnumAxis.values()[index])
    }

    override fun getMetaFromState(state: IBlockState)
            = when (state.getValue(AXIS)) {
                BlockLog.EnumAxis.X -> 0b0100
                BlockLog.EnumAxis.Z -> 0b1000
                BlockLog.EnumAxis.NONE -> 0b1100
                else -> 0b0000
            }

    override fun createBlockState() = BlockStateContainer(this, AXIS)

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            generateBlockStates(this, mapper) {
                when (it.extract("axis=(\\w+)")) {
                    "y" -> "model"(key)

                    "x" -> {
                        "model"(key)
                        "x"(90)
                        "y"(90)
                    }

                    "z" -> {
                        "model"(key)
                        "x"(90)
                    }

                    else -> "model"("${key}_$postFix")
                }
            }
        }, {
            getPathForBlockModel(this) to {
                    "parent"("block/cube_column")
                    "textures" {
                        "end"("$modId:blocks/$key")
                        "side"("$modId:blocks/${key}_$postFix")
                    }
            }
            getPathForBlockModel(this, "${key}_$postFix")(generateBaseBlockModel(this, "${key}_$postFix"))
        })
        return true
    }

    protected open val postFix: String
        get() = "none"
}
