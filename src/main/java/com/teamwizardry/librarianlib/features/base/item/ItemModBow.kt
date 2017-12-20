package com.teamwizardry.librarianlib.features.base.item

import com.google.gson.JsonObject
import com.teamwizardry.librarianlib.core.client.ModelHandler
import com.teamwizardry.librarianlib.features.base.IModelGenerator
import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.kotlin.json
import com.teamwizardry.librarianlib.features.utilities.JsonGenerationUtils
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder sword.
 */
@Suppress("LeakingThis")
open class ItemModBow(name: String) : ItemBow(), IModItemProvider, IModelGenerator {

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, arrayOf(), this::creativeTab)

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

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            variants.indices.mapTo(subItems) { ItemStack(this, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    // Model Generation

    override fun generateMissingItem(variant: String): Boolean {
        val rd = this.registryName!!.resourceDomain
        ModelHandler.generateItemJson(this) {
            mapOf(JsonGenerationUtils.getPathForItemModel(this, variant)
                    to JsonGenerationUtils.generateBaseItemModel(this, variant, "item/bow").apply {
                (this as JsonObject).add("overrides", json {
                    array(
                            obj("predicate" to obj("pulling" to 1), "model" to "$rd:item/${variant}_pulling_0"),
                            obj("predicate" to obj("pulling" to 1, "pull" to 0.65), "model" to "$rd:item/${variant}_pulling_1"),
                            obj("predicate" to obj("pulling" to 1, "pull" to 0.9), "model" to "$rd:item/${variant}_pulling_2"))
                })
            },
                    JsonGenerationUtils.getPathForItemModel(this, variant + "_pulling_0")
                            to JsonGenerationUtils.generateBaseItemModel(this, variant + "_pulling_0", "$rd:item/$variant"),
                    JsonGenerationUtils.getPathForItemModel(this, variant + "_pulling_1")
                            to JsonGenerationUtils.generateBaseItemModel(this, variant + "_pulling_1", "$rd:item/$variant"),
                    JsonGenerationUtils.getPathForItemModel(this, variant + "_pulling_2")
                            to JsonGenerationUtils.generateBaseItemModel(this, variant + "_pulling_2", "$rd:item/$variant"))
        }
        return true
    }
}

