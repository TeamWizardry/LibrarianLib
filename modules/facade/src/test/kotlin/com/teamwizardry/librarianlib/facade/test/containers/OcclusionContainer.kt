package com.teamwizardry.librarianlib.facade.test.containers

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.test.containers.base.TestContainer
import com.teamwizardry.librarianlib.facade.test.containers.base.TestContainerData
import com.teamwizardry.librarianlib.prism.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler

class OcclusionContainer(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestContainer<OcclusionContainer.Data>(Data::class.java, windowId, player, pos) {
    val contentsSlots: SlotManager = SlotManager(data.inventory)

    init {
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