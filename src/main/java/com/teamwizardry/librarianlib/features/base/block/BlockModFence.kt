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
import net.minecraft.block.BlockFence
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World

/**
 * The default implementation for an IModBlock.
 */
@Suppress("LeakingThis")
open class BlockModFence(name: String, val parent: IBlockState) : BlockFence(parent.material, parent.mapColor), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    override val variants: Array<out String>

    override val bareName: String = VariantHelper.toSnakeCase(name)
    val modId = currentModId

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        this.variants = VariantHelper.beginSetupBlock(name, arrayOf())
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

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    override fun isToolEffective(type: String?, state: IBlockState) = parent.block.isToolEffective(type, parent)
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent)

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName!!.resourcePath}").toString()
        val simpleName = registryName!!.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.getPathsForBlockstate(this, mapper).associate {
                it to json {
                    obj(
                            "multipart" to array(
                                    obj(
                                            "apply" to obj(
                                                    "model" to "${registryName}_post"
                                            )
                                    ),
                                    obj(
                                            "when" to obj(
                                                    "north" to "true"
                                            ),
                                            "apply" to obj(
                                                    "model" to "${registryName}_side",
                                                    "uvlock" to true
                                            )
                                    ),
                                    obj(
                                            "when" to obj(
                                                    "east" to "true"
                                            ),
                                            "apply" to obj(
                                                    "model" to "${registryName}_side",
                                                    "y" to 90,
                                                    "uvlock" to true
                                            )
                                    ),
                                    obj(
                                            "when" to obj(
                                                    "south" to "true"
                                            ),
                                            "apply" to obj(
                                                    "model" to "${registryName}_side",
                                                    "y" to 180,
                                                    "uvlock" to true
                                            )
                                    ),
                                    obj(
                                            "when" to obj(
                                                    "west" to "true"
                                            ),
                                            "apply" to obj(
                                                    "model" to "${registryName}_side",
                                                    "y" to 270,
                                                    "uvlock" to true
                                            )
                                    )
                            )
                    )
                }
            }
        }, {
            mapOf(JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_post")
                    to json {
                obj(
                        "parent" to "block/fence_post",
                        "textures" to obj(
                                "texture" to name
                        )
                )
            },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_side")
                            to json {
                        obj(
                                "parent" to "block/fence_side",
                                "textures" to obj(
                                        "texture" to name
                                )
                        )
                    })
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val name = ResourceLocation(parentName!!.resourceDomain, "blocks/${parentName!!.resourcePath}").toString()
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item.providedItem)
                    to json {
                obj(
                        "parent" to "block/fence_inventory",
                        "textures" to obj(
                                "texture" to name
                        )
                )
            })
        }
        return true
    }
}
