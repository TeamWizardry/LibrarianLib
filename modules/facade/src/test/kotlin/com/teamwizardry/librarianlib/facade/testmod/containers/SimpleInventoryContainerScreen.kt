package com.teamwizardry.librarianlib.facade.testmod.containers

import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class SimpleInventoryContainerScreen(screenContainer: SimpleInventoryContainer, inv: PlayerInventory, titleIn: ITextComponent): ContainerScreen<SimpleInventoryContainer>(screenContainer, inv, titleIn) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }
}