package com.teamwizardry.librarianlib.features.base

import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

abstract class ModCreativeTab(postFix: String? = null) : CreativeTabs(currentModId + if (postFix == null) "" else ".${VariantHelper.toSnakeCase(postFix)}") {

    companion object {
        val defaultTabs = mutableMapOf<String, ModCreativeTab>()
    }

    /**
     * Calling this during mod construction will make items from this mod prefer this tab.
     */
    protected fun registerDefaultTab() {
        defaultTabs.put(currentModId, this)
    }

    @SideOnly(Side.CLIENT)
    override fun getTranslatedTabLabel(): String {
        return "item_group.$tabLabel"
    }

    internal lateinit var list: NonNullList<ItemStack>

    abstract val iconStack: ItemStack

    override fun getTabIconItem(): ItemStack {
        return iconStack
    }

    override fun displayAllRelevantItems(list: NonNullList<ItemStack>) {
        this.list = list
        for (item in items)
            addItem(item)
        addEnchantmentBooksToList(list, *(relevantEnchantmentTypes ?: arrayOf()))
    }

    private fun addItem(item: Item) {
        val tempList = nonnullListOf<ItemStack>()
        item.getSubItems(item, this, tempList)
        if (item == tabIconItem.item)
            this.list.addAll(0, list.filterNot { it.isEmpty })
        else
            tempList.filterNotTo(list) { it.isEmpty }
    }

    private val items = ArrayList<Item>()

    fun set(block: Block) {
        val item = Item.getItemFromBlock(block) ?: return
        items.add(item)
        block.setCreativeTab(this)
    }

    fun set(item: Item) {
        items.add(item)
        item.creativeTab = this
    }
}

