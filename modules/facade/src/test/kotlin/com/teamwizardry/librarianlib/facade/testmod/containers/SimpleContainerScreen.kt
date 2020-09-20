package com.teamwizardry.librarianlib.facade.testmod.containers

import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent

class SimpleContainerScreen(screenContainer: SimpleContainer, inv: PlayerInventory, titleIn: ITextComponent): ContainerScreen<SimpleContainer>(screenContainer, inv, titleIn) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }
}