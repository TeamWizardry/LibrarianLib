package com.teamwizardry.librarianlib.common.base.block

import com.teamwizardry.librarianlib.client.core.JsonGenerationUtils
import com.teamwizardry.librarianlib.client.core.ModelHandler
import com.teamwizardry.librarianlib.common.base.IModelGenerator
import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider
import com.teamwizardry.librarianlib.common.util.VariantHelper
import com.teamwizardry.librarianlib.common.util.builders.json
import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.block.Block
import net.minecraft.block.BlockFenceGate
import net.minecraft.block.BlockPlanks
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
open class BlockModFenceGate(name: String, val parent: IBlockState) : BlockFenceGate(BlockPlanks.EnumType.DARK_OAK), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    override val variants: Array<out String>

    override val bareName: String = name
    val modId: String

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        modId = currentModId
        this.variants = VariantHelper.beginSetupBlock(name, arrayOf())
        VariantHelper.finishSetupBlock(this, name, itemForm, creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    /**
     * Override this to have a custom ItemBlock implementation.
     */
    open fun createItemForm(): ItemBlock? {
        return ItemModBlock(this)
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    override val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]


    override fun getMapColor(state: IBlockState) = parent.mapColor
    override fun getMaterial(state: IBlockState) = parent.material
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @SideOnly(Side.CLIENT) override fun isTranslucent(state: IBlockState?) = parent.isTranslucent
    override fun getUseNeighborBrightness(state: IBlockState?) = parent.useNeighborBrightness()
    override fun isToolEffective(type: String?, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = parentName.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                val y = if ("facing=south" in it) 0
                else if ("facing=west" in it) 90
                else if ("facing=north" in it) 180
                else 270

                val inWall = "in_wall=true" in it
                val open = "open=true" in it

                val modelType = "gate_${if (inWall) "wall" else "fence"}_${if (open) "open" else "closed"}"
                json {
                    obj(
                            "model" to "${parentName}_$modelType",
                            "uvlock" to true,
                            *if (y != 0) arrayOf("y" to y) else arrayOf()
                    )
                }
            }
        }, {
            mapOf(
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_gate_fence_closed")
                            to json {
                        obj(
                                "parent" to "block/fence_gate_closed",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_gate_fence_open")
                            to json {
                        obj(
                                "parent" to "block/fence_gate_open",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_gate_wall_closed")
                            to json {
                        obj(
                                "parent" to "block/fence_wall_closed",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_gate_wall_open")
                            to json {
                        obj(
                                "parent" to "block/fence_wall_open",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    })
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val name = parentName.resourcePath
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item.providedItem)
                    to JsonGenerationUtils.generateBaseItemModel(item.providedItem, "${name}_gate_fence_closed"))
        }
        return true
    }
}
