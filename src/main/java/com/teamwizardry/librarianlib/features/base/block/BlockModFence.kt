package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.jsonObject
import com.teamwizardry.librarianlib.features.kotlin.key
import com.teamwizardry.librarianlib.features.utilities.getPathForBlockModel
import com.teamwizardry.librarianlib.features.utilities.getPathForItemModel
import com.teamwizardry.librarianlib.features.utilities.getPathsForBlockstate
import net.minecraft.block.Block
import net.minecraft.block.BlockFence
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

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
open class BlockModFence(name: String, val parent: IBlockState) : BlockFence(parent.material, MapColor.GRAY), IModBlock, IModelGenerator {

    private val parentName = parent.block.key

    override val variants: Array<out String> = VariantHelper.beginSetupBlock(name, arrayOf())

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

    override fun isToolEffective(type: String, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    override fun generateMissingBlockstate(block: IModBlockProvider, mapper: ((block: Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = key.resourcePath

        ModelHandler.generateBlockJson(this, {
            for (path in getPathsForBlockstate(this, mapper))
                path to {
                    "multipart"(
                            jsonObject {
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
            getPathForBlockModel(this, "${simpleName}_post") to jsonObject {
                "parent"("block/fence_post")
                "textures" {
                    "texture"(name)
                }
            }
            getPathForBlockModel(this, "${simpleName}_side") to jsonObject {
                "parent"("block/fence_side")
                "textures" {
                    "texture"(name)
                }
            }
        })
        return true
    }

    override fun generateMissingItem(item: IModItemProvider, variant: String): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        ModelHandler.generateItemJson(item) {
            getPathForItemModel(this) to jsonObject {
                "parent"("block/fence_inventory")
                "textures" {
                    "texture"(name)
                }
            }
        }
        return true
    }
}
