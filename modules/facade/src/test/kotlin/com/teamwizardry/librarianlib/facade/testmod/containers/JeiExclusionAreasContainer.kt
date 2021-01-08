package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.slot.SlotManager
import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainer
import com.teamwizardry.librarianlib.facade.testmod.containers.base.TestContainerData
import com.teamwizardry.librarianlib.prism.Save
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.items.ItemStackHandler

class JeiExclusionAreasContainer(windowId: Int, player: PlayerEntity, pos: BlockPos) :
    TestContainer<JeiExclusionAreasContainer.Data>(Data::class.java, windowId, player, pos) {
    val contentsSlots: SlotManager = SlotManager(data.inventory)

    init {
        addSlots(playerSlots.hotbar)
        addSlots(playerSlots.main)
        addSlots(contentsSlots.slots)

        createTransferRule().from(playerSlots.hotbar).from(playerSlots.main).into(contentsSlots.slots)
        createTransferRule().from(contentsSlots.slots).into(playerSlots.main).into(playerSlots.hotbar)
    }

    class Data: TestContainerData() {
        @Save
        val inventory: ItemStackHandler = ItemStackHandler(1)
    }
}