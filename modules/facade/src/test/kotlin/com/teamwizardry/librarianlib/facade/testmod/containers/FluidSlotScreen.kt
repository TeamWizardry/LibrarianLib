package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.container.layers.FluidSlotLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.container.layers.SlotGridLayer
import com.teamwizardry.librarianlib.facade.container.layers.SlotLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class FluidSlotScreen(
    container: FluidSlotContainer,
    inventory: PlayerInventory,
    title: ITextComponent
) : FacadeContainerScreen<FluidSlotContainer>(container, inventory, title) {

    init {
        val contents = GuiLayer(0, 0, 36, 54)
        contents.add(
            FluidSlotLayer(container.tankSlot, 1, 1, 16, 52, true),
            SlotLayer(container.ioSlots.all.getDirect(0), 19, 1, true),
            SlotLayer(container.ioSlots.all.getDirect(1), 19, 37, true)
        )

        val stack = StackLayout.build(5, 5)
            .vertical()
            .alignCenterX()
            .spacing(4)
            .add(contents)
            .add(SlotGridLayer(0, 0, container.playerSlots.main, 9))
            .add(SlotGridLayer(0, 0, container.playerSlots.hotbar, 9))
            .fit()
            .build()
        main.size = stack.size + vec(10, 10)
        main.add(stack)
    }
}