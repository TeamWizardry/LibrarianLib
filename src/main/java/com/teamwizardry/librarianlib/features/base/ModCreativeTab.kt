package com.teamwizardry.librarianlib.features.base

import com.teamwizardry.librarianlib.features.base.block.IModBlockProvider
import com.teamwizardry.librarianlib.features.base.item.IModItemProvider
import com.teamwizardry.librarianlib.features.helpers.VariantHelper
import com.teamwizardry.librarianlib.features.helpers.currentModId
import com.teamwizardry.librarianlib.features.helpers.nonnullListOf
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

abstract class ModCreativeTab(postFix: String? = null) : CreativeTabs(currentModId + if (postFix == null) "" else ".${VariantHelper.toSnakeCase(postFix)}") {

    companion object {
        val defaultTabs = mutableMapOf<String, ModCreativeTab>()

        val itemsToTab = mutableMapOf<IModItemProvider, () -> ModCreativeTab?>()
        val blocksToTab = mutableMapOf<IModBlockProvider, () -> ModCreativeTab?>()

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
    override fun getTranslationKey(): String = "item_group.$tabLabel"

    private lateinit var list: NonNullList<ItemStack>

    abstract val iconStack: ItemStack

    private val lazyStack by lazy { iconStack }

    override fun createIcon(): ItemStack = lazyStack

    override fun displayAllRelevantItems(list: NonNullList<ItemStack>) {
        this.list = list
        for (item in items)
            addItem(item)
        Items.ENCHANTED_BOOK.getSubItems(this, list)
    }

    private fun addItem(item: Item) {
        val tempList = nonnullListOf<ItemStack>()
        item.getSubItems(this, tempList)
        if (item == iconStack.item)
            this.list.addAll(0, tempList.filterNot { it.isEmpty })
        else
            tempList.filterNotTo(list) { it.isEmpty }
    }

    private val items = ArrayList<Item>()

    fun set(block: IModBlockProvider) {
        val item = block.itemForm ?: return
        items.add(item)
        block.providedBlock.setCreativeTab(this)
        item.creativeTab = this
    }

    fun set(item: IModItemProvider) {
        items.add(item.providedItem)
        item.providedItem.creativeTab = this
    }
}

