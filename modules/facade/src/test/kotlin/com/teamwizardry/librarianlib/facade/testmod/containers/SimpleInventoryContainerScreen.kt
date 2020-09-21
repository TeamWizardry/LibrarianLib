package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class SimpleInventoryContainerScreen(
    container: SimpleInventoryContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<SimpleInventoryContainer>(container, inventory, title) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }
}