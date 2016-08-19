package com.teamwizardry.librarianlib.common.base

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Loader
import java.util.*

abstract class ModCreativeTab(postFix: String? = null) : CreativeTabs(Loader.instance().activeModContainer().modId + if (postFix == null) "" else ".$postFix") {

    internal lateinit var list: MutableList<ItemStack>

    override abstract fun getIconItemStack(): ItemStack

    override fun getTabIconItem(): Item? {
        return this.iconItemStack.item
    }

    override fun displayAllRelevantItems(list: MutableList<ItemStack>) {
        this.list = list
        for (item in items)
            addItem(item)
        addEnchantmentBooksToList(list, *(relevantEnchantmentTypes ?: arrayOf()))
    }

    private fun addItem(item: Item) {
        val tempList = mutableListOf<ItemStack>()
        item.getSubItems(item, this, tempList)
        if (item == tabIconItem)
            this.list.addAll(0, tempList)
        else
            this.list.addAll(tempList)
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

