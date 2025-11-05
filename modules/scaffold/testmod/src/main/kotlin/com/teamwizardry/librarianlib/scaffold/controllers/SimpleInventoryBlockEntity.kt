package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.container.DefaultInventoryImpl
import com.teamwizardry.librarianlib.scribe.Save
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class SimpleInventoryBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state) {

    @Save("items")
    val items: DefaultedList<ItemStack> = DefaultedList.ofSize(16, ItemStack.EMPTY)
    val inventory: Inventory = DefaultInventoryImpl.of(items)
}
