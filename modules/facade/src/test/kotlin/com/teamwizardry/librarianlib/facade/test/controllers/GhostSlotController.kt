package com.teamwizardry.librarianlib.facade.test.controllers

import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestController
import com.teamwizardry.librarianlib.facade.test.controllers.base.TestControllerData
import com.teamwizardry.librarianlib.prism.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler

class GhostSlotController(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestController<GhostSlotController.Data>(Data::class.java, windowId, player, pos) {
    val contentsSlots: SlotManager = SlotManager(data.inventory)

    init {
        contentsSlots.all.setFactory(::GhostSlot)

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.all)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.all)
        createTransferRule().from(contentsSlots.all).into(playerSlots.main).into(playerSlots.hotbar)
    }

    class Data: TestControllerData() {
        @Save
        val inventory: ItemStackHandler = ItemStackHandler(5)
    }
}