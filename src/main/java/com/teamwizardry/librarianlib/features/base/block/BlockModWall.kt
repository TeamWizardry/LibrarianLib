package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import com.teamwizardry.librarianlib.features.utilities.getPathsForBlockstate
import net.minecraft.block.Block
import net.minecraft.block.BlockFenceGate
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


/**
 * @author WireSegal
 * Created at 10:36 AM on 5/7/16.
 */
open class BlockModWall(name: String, val parent: IBlockState) : BlockMod(name, parent.material), IModelGenerator {
    private val parentName = parent.block.key

    companion object {
        val UP: PropertyBool = PropertyBool.create("up")
        val NORTH: PropertyBool = PropertyBool.create("north")
        val EAST: PropertyBool = PropertyBool.create("east")
        val SOUTH: PropertyBool = PropertyBool.create("south")
        val WEST: PropertyBool = PropertyBool.create("west")

        protected val AABB_BY_INDEX = arrayOf(AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75), AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 1.0), AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 0.75), AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 1.0), AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 0.75), AxisAlignedBB(0.3125, 0.0, 0.0, 0.6875, 0.875, 1.0), AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 0.75), AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 1.0), AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 0.75), AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 1.0), AxisAlignedBB(0.0, 0.0, 0.3125, 1.0, 0.875, 0.6875), AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0), AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 0.75), AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 1.0), AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75), AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
        protected val CLIP_AABB_BY_INDEX = arrayOf(AABB_BY_INDEX[0].setMaxY(1.5), AABB_BY_INDEX[1].setMaxY(1.5), AABB_BY_INDEX[2].setMaxY(1.5), AABB_BY_INDEX[3].setMaxY(1.5), AABB_BY_INDEX[4].setMaxY(1.5), AABB_BY_INDEX[5].setMaxY(1.5), AABB_BY_INDEX[6].setMaxY(1.5), AABB_BY_INDEX[7].setMaxY(1.5), AABB_BY_INDEX[8].setMaxY(1.5), AABB_BY_INDEX[9].setMaxY(1.5), AABB_BY_INDEX[10].setMaxY(1.5), AABB_BY_INDEX[11].setMaxY(1.5), AABB_BY_INDEX[12].setMaxY(1.5), AABB_BY_INDEX[13].setMaxY(1.5), AABB_BY_INDEX[14].setMaxY(1.5), AABB_BY_INDEX[15].setMaxY(1.5))

        private fun getAABBIndex(state: IBlockState): Int {
            var i = 0

            if (state.getValue(NORTH))
                i = i or (1 shl EnumFacing.NORTH.horizontalIndex)

            if (state.getValue(EAST))
                i = i or (1 shl EnumFacing.EAST.horizontalIndex)

            if (state.getValue(SOUTH))
                i = i or (1 shl EnumFacing.SOUTH.horizontalIndex)

            if (state.getValue(WEST))
                i = i or (1 shl EnumFacing.WEST.horizontalIndex)

            return i
        }
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState, pos: BlockPos, side: EnumFacing): BlockFaceShape {
        return if (side != EnumFacing.UP && side != EnumFacing.DOWN) BlockFaceShape.MIDDLE_POLE_THICK else BlockFaceShape.CENTER_BIG
    }

    override fun getMetaFromState(state: IBlockState) = 0

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = AABB_BY_INDEX[getAABBIndex(getActualState(state, source, pos))]

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB = CLIP_AABB_BY_INDEX[getAABBIndex(getActualState(blockState, worldIn, pos))]

    override fun canPlaceTorchOnTop(state: IBlockState, world: IBlockAccess, pos: BlockPos) = true
    @Suppress("OverridingDeprecatedMember")
    override fun isFullCube(state: IBlockState) = false

    override fun isPassable(worldIn: IBlockAccess, pos: BlockPos) = false
    @Suppress("OverridingDeprecatedMember")
    override fun isOpaqueCube(state: IBlockState) = false

    private fun canConnectTo(worldIn: IBlockAccess, pos: BlockPos): Boolean {
        val iblockstate = worldIn.getBlockState(pos)
        val block = iblockstate.block
        val material = iblockstate.material

        return if (block === Blocks.BARRIER) false
        else if (block !== this && block !is BlockFenceGate)
            if (material.isOpaque && iblockstate.isFullCube)
                material !== Material.GOURD
            else block is BlockModWall
        else true
    }


    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) = parent.getMapColor(worldIn, pos)

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)

    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return if (side == EnumFacing.DOWN) super.shouldSideBeRendered(blockState, blockAccess, pos, side) else true
    }

    @Suppress("OverridingDeprecatedMember")
    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val flag = canConnectTo(worldIn, pos.north())
        val flag1 = canConnectTo(worldIn, pos.east())
        val flag2 = canConnectTo(worldIn, pos.south())
        val flag3 = canConnectTo(worldIn, pos.west())
        val flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3
        return state.withProperty(UP, !flag4 || !worldIn.isAirBlock(pos.up())).withProperty(NORTH, flag).withProperty(EAST, flag1).withProperty(SOUTH, flag2).withProperty(WEST, flag3)
    }

    override fun createBlockState() = BlockStateContainer(this, UP, NORTH, EAST, WEST, SOUTH)


    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = key.resourcePath

        ModelHandler.generateBlockJson(this, {
            for (path in getPathsForBlockstate(this, mapper))
                path to {
                    "multipart"(
                            jsonObject {
                                "when" {
                                    "up"("true")
                                }
                                "apply" {
                                    "model"("${key}_post")
                                }
                            },
                            jsonObject {
                                "when" {
                                    "north"("true")
                                }
                                "apply" {
                                    "model"("${key}_side")
                                    "uvlock"(true)
                                }
                            },
                            jsonObject {
                                "when" {
                                    "east"("true")
                                }
                                "apply" {
                                    "model"("${key}_side")
                                    "y"(90)
                                    "uvlock"(true)
                                }
                            },
                            jsonObject {
                                "when" {
                                    "south"("true")
                                }
                                "apply" {
                                    "model"("${key}_side")
                                    "y"(180)
                                    "uvlock"(true)
                                }
                            },
                            jsonObject {
                                "when" {
                                    "west"("true")
                                }
                                "apply" {
                                    "model"("${key}_side")
                                    "y"(270)
                                    "uvlock"(true)
                                }
                            }
                    )
                }
        }, {
            getPathForBlockModel(this, "${simpleName}_post") to {
                "parent"("block/wall_post")
                "textures" {
                    "wall"(name)
                }

            }
            getPathForBlockModel(this, "${simpleName}_side") to {
                "parent"("block/wall_side")
                "textures" {
                    "wall"(name)
                }
            }
        })
        return true
    }

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "block/${parentName.resourcePath}").toString()
        ModelHandler.generateItemJson(item) {
            getPathForItemModel(this) to jsonObject {
                "parent"("block/wall_inventory")
                "textures" {
                    "wall"(name)
                }
            }
        }
        return true
    }
}
