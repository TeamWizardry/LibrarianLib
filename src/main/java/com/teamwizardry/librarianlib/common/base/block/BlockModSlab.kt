package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.client.core.JsonGenerationUtils
import com.teamwizardry.librarianlib.client.core.JsonGenerationUtils.getPathForBlockModel
import com.teamwizardry.librarianlib.client.core.ModelHandler
import com.teamwizardry.librarianlib.common.base.IModelGenerator
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider
import com.teamwizardry.librarianlib.common.util.builders.json
import net.minecraft.block.Block
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * The default implementation for an IModBlock slab.
 */
open class BlockModSlab(name: String, val parent: IBlockState) : BlockMod(name, parent.material, parent.mapColor), IModelGenerator {

    companion object {
        val STATE: PropertyEnum<SlabType> = PropertyEnum.create("state", SlabType::class.java)
    }

    private val parentName = parent.block.registryName

    override fun createBlockState() = BlockStateContainer(this, STATE)
    override fun createItemForm() = ItemModSlab(this)
    override fun canSilkHarvest() = false

    override fun isFullBlock(state: IBlockState) = state.isOpaqueCube

    override fun getLightOpacity(state: IBlockState, world: IBlockAccess, pos: BlockPos) = parent.getLightOpacity(world, pos)
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @SideOnly(Side.CLIENT) override fun isTranslucent(state: IBlockState?) = super.isTranslucent(state)
    override fun getUseNeighborBrightness(state: IBlockState?) = parent.useNeighborBrightness()


    protected val AABB_BOTTOM_HALF = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
    protected val AABB_TOP_HALF = AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0)

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos)
            = if (state.isOpaqueCube)
                Block.FULL_BLOCK_AABB
            else if (state.getValue(STATE) == SlabType.TOP)
                AABB_TOP_HALF
            else
                AABB_BOTTOM_HALF

    /**
     * Checks if an IBlockState represents a block that is opaque and a full cube.
     */
    override fun isFullyOpaque(state: IBlockState) = state.isOpaqueCube || state.getValue(STATE) == SlabType.TOP

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    override fun isOpaqueCube(state: IBlockState) = state.getValue(STATE) == SlabType.FULL

    override fun doesSideBlockRendering(state: IBlockState, world: IBlockAccess, pos: BlockPos, face: EnumFacing): Boolean {
        if (state.isOpaqueCube)
            return true

        val side = state.getValue(STATE)
        return side == SlabType.TOP && face == EnumFacing.UP || side == SlabType.BOTTOM && face == EnumFacing.DOWN
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    override fun getStateForPlacement(worldIn: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState {
        val iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
        return if (iblockstate.isOpaqueCube)
            iblockstate
        else if (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || hitY.toDouble() <= 0.5))
            iblockstate.withProperty(STATE, SlabType.BOTTOM)
        else
            iblockstate.withProperty(STATE, SlabType.TOP)
    }

    override fun quantityDropped(state: IBlockState, fortune: Int, random: Random?) = if (state.isOpaqueCube) 2 else 1

    override fun isFullCube(state: IBlockState) = state.isOpaqueCube

    override fun getMetaFromState(state: IBlockState) = state.getValue(STATE).ordinal
    override fun getStateFromMeta(meta: Int) = defaultState.withProperty(STATE, SlabType.values()[meta % SlabType.values().size])

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean
            = if (blockState.isOpaqueCube)
                super.shouldSideBeRendered(blockState, blockAccess, pos, side)
            else if (side != EnumFacing.UP && side != EnumFacing.DOWN && !super.shouldSideBeRendered(blockState, blockAccess, pos, side))
                false
            else
                super.shouldSideBeRendered(blockState, blockAccess, pos, side)

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = parentName.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                when (it) {
                    "state=bottom" -> json { obj("model" to "${parentName}_bottom") }
                    "state=top" -> json { obj("model" to "${parentName}_top") }
                    "state=full" -> json { obj("model" to "${parentName}_full") }
                    else -> json { obj() }
                }
            }
        }, {
            mapOf(
                    getPathForBlockModel(this, "${simpleName}_bottom") to json {
                        obj(
                                "parent" to "block/half_slab",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    getPathForBlockModel(this, "${simpleName}_top") to json {
                        obj(
                                "parent" to "block/upper_slab",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    getPathForBlockModel(this, "${simpleName}_full") to json {
                        obj(
                                "parent" to "block/cube_all",
                                "textures" to obj(
                                        "all" to name
                                )
                        )
                    }
            )
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val item = itemForm as? IModItemProvider ?: return false
        val name = ResourceLocation(parentName.resourceDomain, "block/${parentName.resourcePath}").toString()
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item as Item) to json { obj("parent" to name + "_bottom") })
        }
        return true
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return parent.block.isToolEffective(type, parent)
    }

    override fun getHarvestTool(state: IBlockState): String {
        return parent.block.getHarvestTool(parent)
    }
}

enum class SlabType : EnumStringSerializable {
    BOTTOM, TOP, FULL
}
