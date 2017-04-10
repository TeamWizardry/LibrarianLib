package com.teamwizardry.librarianlib.features.base.block

import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.base.block.BlockModSlab.Companion.wrapMaterial
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockPane
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * @author WireSegal
 * Created at 3:53 PM on 2/14/17.
 */
@Suppress("LeakingThis")
open class BlockModPane(name: String, canDrop: Boolean, val parent: IBlockState) : BlockPane(wrapMaterial(parent.material), canDrop), IModBlock, IModelGenerator {

    private val parentName = parent.block.registryName

    val modId: String = currentModId
    override val bareName: String = VariantHelper.toSnakeCase(name)
    override val variants: Array<out String> = VariantHelper.beginSetupBlock(name, arrayOf())

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
        VariantHelper.finishSetupBlock(this, bareName, itemForm, creativeTab)
    }

    override fun setUnlocalizedName(name: String): Block {
        super.setUnlocalizedName(name)
        VariantHelper.setUnlocalizedNameForBlock(this, modId, name, itemForm)
        return this
    }

    override fun getExplosionResistance(world: World, pos: BlockPos, exploder: Entity, explosion: Explosion) = parent.block.getExplosionResistance(world, pos, exploder, explosion)
    override fun getBlockHardness(blockState: IBlockState, worldIn: World, pos: BlockPos) = parent.getBlockHardness(worldIn, pos)
    @SideOnly(Side.CLIENT) override fun isTranslucent(state: IBlockState?) = parent.isTranslucent
    override fun isToolEffective(type: String?, state: IBlockState) = parent.block.isToolEffective(type, parent) || (blockMaterial == BlockModSlab.FAKE_WOOD && type == "axe")
    override fun getHarvestTool(state: IBlockState): String? = parent.block.getHarvestTool(parent) ?: if (blockMaterial == BlockModSlab.FAKE_WOOD) "axe" else null

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

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = registryName.resourcePath

        ModelHandler.generateBlockJson(this, {
            mapOf(JsonGenerationUtils.getPathForBaseBlockstate(this) to json {
                obj(
                        "multipart" to array(
                                obj("apply" to obj("model" to "${registryName}_post")),
                                obj("when" to obj("north" to true), "apply" to obj("model" to "${registryName}_side")),
                                obj("when" to obj("east" to true), "apply" to obj("model" to "${registryName}_side", "y" to 90)),
                                obj("when" to obj("south" to true), "apply" to obj("model" to "${registryName}_side_alt")),
                                obj("when" to obj("west" to true), "apply" to obj("model" to "${registryName}_side_alt", "y" to 90)),
                                obj("when" to obj("north" to false), "apply" to obj("model" to "${registryName}_noside")),
                                obj("when" to obj("east" to false), "apply" to obj("model" to "${registryName}_noside_alt")),
                                obj("when" to obj("south" to false), "apply" to obj("model" to "${registryName}_noside_alt", "y" to 90)),
                                obj("when" to obj("west" to false), "apply" to obj("model" to "${registryName}_noside", "y" to 270))
                        )
                )
            })
        }, {
            mapOf(
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_post") to json {
                        obj(
                                "parent" to "block/pane_post",
                                "textures" to obj(
                                        "edge" to name + "_top",
                                        "pane" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_side") to json {
                        obj(
                                "parent" to "block/pane_side",
                                "textures" to obj(
                                        "edge" to name + "_top",
                                        "pane" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_side_alt") to json {
                        obj(
                                "parent" to "block/pane_side_alt",
                                "textures" to obj(
                                        "edge" to name + "_top",
                                        "pane" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_noside") to json {
                        obj(
                                "parent" to "block/pane_noside",
                                "textures" to obj(
                                        "pane" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_noside_alt") to json {
                        obj(
                                "parent" to "block/pane_noside_alt",
                                "textures" to obj(
                                        "pane" to name
                                )
                        )
                    }
            )
        })
        return true
    }

    override fun generateMissingItem(variant: String): Boolean {
        val item = itemForm as? IModItemProvider ?: return false
        ModelHandler.generateItemJson(item) {
            mapOf(JsonGenerationUtils.getPathForItemModel(item as Item, variant) to
                    JsonGenerationUtils.generateRegularItemModel(item, variant))
        }
        return true
    }
}
