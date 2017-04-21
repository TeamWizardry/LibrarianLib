package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder item.
 */
@Suppress("LeakingThis")
open class ItemMod(name: String, vararg variants: String) : Item(), IModItemProvider {

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, variants, this::creativeTab)

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

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: NonNullList<ItemStack>) {
        variants.indices.mapTo(subItems) { ItemStack(itemIn, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    open val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]
}

