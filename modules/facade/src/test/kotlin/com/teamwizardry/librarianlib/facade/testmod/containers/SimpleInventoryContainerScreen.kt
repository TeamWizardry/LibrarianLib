package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.RectLayer
import com.teamwizardry.librarianlib.facade.layers.SlotGridLayer
import com.teamwizardry.librarianlib.facade.layers.StackLayout
import com.teamwizardry.librarianlib.facade.layers.StackLayoutBuilder
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import java.awt.Color

class SimpleInventoryContainerScreen(
    container: SimpleInventoryContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<SimpleInventoryContainer>(container, inventory, title) {
    init {
        val stack = StackLayout.build()
            .vertical()
            .alignCenterX()
            .spacing(4)
            .add(SlotGridLayer(0, 0, container.contentsSlots.slots, 8, 2))
            .add(SlotGridLayer(0, 0, container.playerSlots.main, 9, 2))
            .add(SlotGridLayer(0, 0, container.playerSlots.hotbar, 9, 2))
            .fit()
            .build()
        main.add(stack)
        main.size = stack.size
        main.add(RectLayer(Color.GRAY, 0, 0, main.widthi, main.heighti))
        val overlay = GuiLayer()
        overlay.ignoreMouseOverBounds = true
        overlay.zIndex = 1000.0
        overlay.hook<GuiLayerEvents.Update> {
            overlay.frame = main.frame
        }
        overlay.add(RectLayer(Color(1f, 0f, 0f, 0.2f), -20, -20, main.widthi/2 + 20, main.heighti/2 + 20))
        facade.root.add(overlay)
    }
}