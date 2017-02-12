package com.teamwizardry.librarianlib.common.base.item

import com.teamwizardry.librarianlib.client.core.JsonGenerationUtils
import com.teamwizardry.librarianlib.client.core.ModelHandler
import com.teamwizardry.librarianlib.common.base.IModelGenerator
import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.util.VariantHelper
import com.teamwizardry.librarianlib.common.util.builders.serialize
import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemTool
import java.io.File

/**
 * The default implementation for an IVariantHolder tool.
 */
@Suppress("LeakingThis")
open class ItemModTool(name: String, attackDamage: Float, attackSpeed: Float, toolMaterial: ToolMaterial, effectiveBlocks: Set<Block>, vararg variants: String) : ItemTool(attackDamage, attackSpeed, toolMaterial, effectiveBlocks), IModItemProvider, IModelGenerator {

    constructor(name: String, toolMaterial: ToolMaterial, effectiveBlocks: Set<Block>, vararg variants: String) : this(name, 0F, 0F, toolMaterial, effectiveBlocks, *variants)

    override val providedItem: Item
        get() = this

    private val bareName = name
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, name, variants, creativeTab)

    override fun setUnlocalizedName(name: String): Item {
        VariantHelper.setUnlocalizedNameForItem(this, modId, name)
        return super.setUnlocalizedName(name)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.bareName else variants[dmg]

        return "item.$modId:$name"
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        variants.indices.mapTo(subItems) { ItemStack(itemIn, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    // Model Generation

    override fun generateMissingItem(variant: String): Boolean {
        ModelHandler.generateItemJson(this) {
            mapOf(JsonGenerationUtils.getPathForItemModel(this, variant)
                    to JsonGenerationUtils.generateBaseItemModel(this, variant, "item/handheld"))
        }
        return true
    }
}

