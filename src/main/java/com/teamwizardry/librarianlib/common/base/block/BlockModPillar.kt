package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.client.core.JsonGenerationUtils
import com.teamwizardry.librarianlib.client.core.ModelHandler
import com.teamwizardry.librarianlib.common.base.IModelGenerator
import com.teamwizardry.librarianlib.common.util.builders.json
import com.teamwizardry.librarianlib.common.util.flatAssociate
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

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                if ("axis=y" in it) json {
                    obj(
                            "model" to registryName.toString()
                    )
                } else if ("axis=x" in it) json {
                    obj(
                            "model" to registryName.toString(),
                            "x" to 90,
                            "y" to 90
                    )
                } else if ("axis=z" in it) json {
                    obj(
                            "model" to registryName.toString(),
                            "x" to 90
                    )
                } else json {
                    obj(
                            "model" to registryName.toString() + "_$postFix"
                    )
                }
            }
        }, {
            variants.flatAssociate {
                listOf(
                        JsonGenerationUtils.getPathForBlockModel(this)
                                to json {
                            obj(
                                    "parent" to "block/cube_column",
                                    "textures" to obj(
                                            "end" to "$modId:blocks/$it",
                                            "side" to "$modId:blocks/${it}_$postFix"
                                    )
                            )
                        },
                        JsonGenerationUtils.getPathForBlockModel(this, "${it}_$postFix")
                                to JsonGenerationUtils.generateBaseBlockModel(this, "${it}_$postFix"))
            }
        })
        return true
    }

    protected open val postFix: String
        get() = "none"
}
