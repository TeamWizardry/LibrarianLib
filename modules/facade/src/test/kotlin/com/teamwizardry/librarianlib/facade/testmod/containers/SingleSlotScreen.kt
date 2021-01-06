package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.layers.SlotGridLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class SingleSlotScreen(
    container: SingleSlotContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<SingleSlotContainer>(container, inventory, title) {

    init {
        val stack = StackLayout.build(5, 5)
            .vertical()
            .alignCenterX()
            .spacing(4)
            .add(SlotGridLayer(0, 0, container.contentsSlots.slots, 1))
            .add(SlotGridLayer(0, 0, container.playerSlots.main, 9))
            .add(SlotGridLayer(0, 0, container.playerSlots.hotbar, 9))
            .fit()
            .build()
        main.size = stack.size + vec(10, 10)
        main.add(stack)
    }
}