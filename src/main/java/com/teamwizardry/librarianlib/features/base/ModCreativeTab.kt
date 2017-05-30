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

        val itemsToTab = mutableMapOf<Item, () -> ModCreativeTab?>()
        val blocksToTab = mutableMapOf<Block, () -> ModCreativeTab?>()

        fun latePre() {
            for ((item, function) in itemsToTab) function()?.set(item)
            for ((block, function) in blocksToTab) function()?.set(block)
        }
    }

    init {
        if (postFix == null) registerDefaultTab()
    }

    /**
     * Calling this during mod construction will make items from this mod prefer this tab.
     * If the postFix specified in the constructor is null, this will be called by default.
     */
    protected fun registerDefaultTab() {
        defaultTabs.put(currentModId, this)
    }

    @SideOnly(Side.CLIENT)
    override fun getTranslatedTabLabel(): String = "item_group.$tabLabel"

    private lateinit var list: NonNullList<ItemStack>

    abstract val iconStack: ItemStack

    private val lazyStack by lazy { iconStack }

    override fun getTabIconItem(): ItemStack = lazyStack

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
            this.list.addAll(0, tempList.filterNot { it.isEmpty })
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

