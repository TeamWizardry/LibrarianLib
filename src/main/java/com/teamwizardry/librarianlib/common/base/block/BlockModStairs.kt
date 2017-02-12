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
import net.minecraft.block.BlockStairs
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation

/**
 * @author WireSegal
 * Created at 10:36 AM on 1/10/17.
 */
@Suppress("LeakingThis")
open class BlockModStairs(name: String, parent: IBlockState) : BlockStairs(parent), IModBlock, IModelGenerator {

    override val variants: Array<out String>

    private val parentName = parent.block.registryName

    override val bareName: String = name
    val modId = currentModId

    val itemForm: ItemBlock? by lazy { createItemForm() }

    init {
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

    override fun generateMissingBlockstate(mapper: ((Block) -> Map<IBlockState, ModelResourceLocation>)?): Boolean {
        val name = ResourceLocation(parentName.resourceDomain, "blocks/${parentName.resourcePath}").toString()
        val simpleName = registryName.resourcePath

        ModelHandler.generateBlockJson(this, {
            JsonGenerationUtils.generateBlockStates(this, mapper) {
                val x = if ("half=top" in it) 180 else 0
                var y = if ("facing=east" in it) 0
                    else if ("facing=west" in it) 180
                    else if ("facing=south" in it) 90
                    else 270
                if ("half=top" in it && ("shape=inner" in it || "shape=outer" in it)) y += 90
                if ("_left" in it) y += 270
                y %= 360

                val modelType = if ("shape=straight" in it) "" else if ("shape=inner" in it) "_inner" else "_outer"
                json { obj(
                        "model" to "$registryName$modelType",
                        *if (x != 0) arrayOf("x" to x) else arrayOf(),
                        *if (y != 0) arrayOf("y" to y) else arrayOf(),
                        *if (x != 0 || y != 0) arrayOf("uvlock" to true) else arrayOf()
                ) }
            }
        }, {
            mapOf(
                    JsonGenerationUtils.getPathForBlockModel(this, simpleName) to json {
                        obj(
                                "parent" to "block/stairs",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_inner") to json {
                        obj(
                                "parent" to "block/inner_stairs",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
                                )
                        )
                    },
                    JsonGenerationUtils.getPathForBlockModel(this, "${simpleName}_outer") to json {
                        obj(
                                "parent" to "block/outer_stairs",
                                "textures" to obj(
                                        "bottom" to name,
                                        "top" to name,
                                        "side" to name
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
            mapOf(JsonGenerationUtils.getPathForItemModel(item as Item)
                    to JsonGenerationUtils.generateBaseItemModel(item))
        }
        return true
    }
}
