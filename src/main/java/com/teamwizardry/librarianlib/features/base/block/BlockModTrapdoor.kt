package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockTrapDoor
import net.minecraft.block.material.MapColor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
open class BlockModTrapdoor(name: String, val parent: IBlockState) : BlockTrapDoor(parent.material), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    override val variants = VariantHelper.beginSetupBlock(name, arrayOf())

    override val bareName: String = VariantHelper.toSnakeCase(name)
    val modId = currentModId

    override val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, this::creativeTab)
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

    @Suppress("OverridingDeprecatedMember")
    override fun getMapColor(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): MapColor = parent.getMapColor(worldIn, pos)
    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity?, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    @Suppress("OverridingDeprecatedMember")
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @SideOnly(Side.CLIENT)
    @Suppress("OverridingDeprecatedMember")
    override fun isTranslucent(state: IBlockState) = parent.isTranslucent
    @Suppress("OverridingDeprecatedMember")
    override fun getUseNeighborBrightness(state: IBlockState) = parent.useNeighborBrightness()
    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = registryName!!.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                if ("half=bottom" in it && "open=false" in it) {
                    json {
                        obj(
                                "model" to "${registryName}_bottom"
                        )
                    }
                } else if ("half=top" in it && "open=false" in it) {
                    json {
                        obj(
                                "model" to "${registryName}_top"
                        )
                    }
                } else if ("facing=north" in it) {
                    json {
                        obj(
                                "model" to "${registryName}_open"
                        )
                    }
                } else if ("facing=south" in it) {
                    json {
                        obj(
                                "model" to "${registryName}_open",
                                "y" to 180
                        )
                    }
                } else if ("facing=east" in it) {
                    json {
                        obj(
                                "model" to "${registryName}_open",
                                "y" to 90
                        )
                    }
                } else {
                    json {
                        obj(
                                "model" to "${registryName}_open",
                                "y" to 270
                        )
                    }
                }
            }
        }, {
            mapOf(JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_bottom")
                    to json {
                obj(
                        "parent" to "block/trapdoor_bottom",
                        "textures" to obj(
                                "texture" to name
                        )
                )
            },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_top")
                            to json {
                        obj(
                                "parent" to "block/trapdoor_top",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_open")
                            to json {
                        obj(
                                "parent" to "block/trapdoor_open",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    })
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val name = ResourceLocation(registryName!!.resourceDomain, "block/${registryName!!.resourcePath}").toString()
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item.providedItem)
                    to json {
                obj(
                        "parent" to name + "_bottom"
                )
            })
        }
        return true
    }
}
