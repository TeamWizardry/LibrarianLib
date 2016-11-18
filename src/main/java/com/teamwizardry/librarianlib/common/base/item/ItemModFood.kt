package com.teamwizardry.librarianlib.common.base.item

import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.util.VariantHelper
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Loader

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

    override val variants: Array<out String>

    private val bareName: String
    private val modId: String

    init {
        modId = Loader.instance().activeModContainer().modId
        bareName = name
        this.variants = VariantHelper.setupItem(this, name, variants, creativeTab)
    }

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
}

