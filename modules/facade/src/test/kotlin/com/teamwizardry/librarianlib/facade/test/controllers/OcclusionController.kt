package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.container.DefaultInventoryImpl
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestController
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerData
import com.teamwizardry.librarianlib.scribe.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

class OcclusionController(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestController<OcclusionController.Data>(Data::class.java, windowId, player, pos) {
    val contentsSlots: SlotManager = SlotManager(data.inventory)

    init {
        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.all)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.all)
        createTransferRule().from(contentsSlots.all).into(playerSlots.main).into(playerSlots.hotbar)
    }

    class Data: TestControllerData() {
        @Save
        val items: DefaultedList<ItemStack> = DefaultedList.ofSize(5, ItemStack.EMPTY)
        val inventory: Inventory = DefaultInventoryImpl.of(items)
    }
}