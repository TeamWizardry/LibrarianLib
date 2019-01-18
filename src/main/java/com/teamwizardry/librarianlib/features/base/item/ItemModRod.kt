package com.teamwizardry.librarianlib.features.base.item

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemFishingRod
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * The default implementation for an IVariantHolder rod.
 */
@Suppress("LeakingThis")
open class ItemModRod(name: String, maxDamage: Int) : ItemFishingRod(), IModItemProvider {

    override val providedItem: Item
        get() = this

    init {
        setMaxDamage(maxDamage)
    }

    private val bareName = VariantHelper.toSnakeCase(name)
    private val modId = currentModId
    override val variants = VariantHelper.setupItem(this, bareName, arrayOf(), this::creativeTab)

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

