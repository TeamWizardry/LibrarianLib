package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.containers.base.TestContainer
import com.teamwizardry.librarianlib.facade.test.containers.base.TestContainerData
import com.teamwizardry.librarianlib.prism.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler

class GhostSlotContainer(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestContainer<GhostSlotContainer.Data>(Data::class.java, windowId, player, pos) {
    val contentsSlots: SlotManager = SlotManager(data.inventory)

    init {
        contentsSlots.all.setFactory(::GhostSlot)

        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.all)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.all)
        createTransferRule().from(contentsSlots.all).into(playerSlots.main).into(playerSlots.hotbar)
    }

    class Data: TestContainerData() {
        @Save
        val inventory: ItemStackHandler = ItemStackHandler(5)
    }
}