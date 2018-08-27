package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder food.
 */
@Suppress("LeakingThis")
open class ItemModFood(name: String, amount: Int, saturation: Float, wolfFood: Boolean, vararg variants: String) : ItemFood(amount, saturation, wolfFood), IModItemProvider {

    constructor(name: String, amount: Int, wolfFood: Boolean, vararg variants: String) : this(name, amount, 0.6F, wolfFood, *variants)
    constructor(name: String, amount: Int, saturation: Float, vararg variants: String) : this(name, amount, saturation, false, *variants)
    constructor(name: String, amount: Int, vararg variants: String) : this(name, amount, 0.6F, false, *variants)

    override val providedItem: Item
        get() = this

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, variants, this::creativeTab)

    override fun setTranslationKey(name: String): Item {
        VariantHelper.setTranslationKeyForItem(this, modId, name)
        return super.setTranslationKey(name)
    }

    override fun getTranslationKey(stack: ItemStack): String {
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
}

