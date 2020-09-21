package com.teamwizardry.librarianlib.facade.testmod.containers

import com.teamwizardry.librarianlib.facade.container.FacadeContainerScreen
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

class SimpleContainerScreen(
    container: SimpleContainer,
    inventory: PlayerInventory,
    title: ITextComponent
): FacadeContainerScreen<SimpleContainer>(container, inventory, title) {
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val position = vec(mouseX.toInt(), mouseY.toInt())
        player.sendMessage(StringTextComponent("[GUI] mouseClicked: $position, $button"))
        sendMessage("mouseClicked", position, button)
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val position = vec(mouseX.toInt(), mouseY.toInt())
        player.sendMessage(StringTextComponent("[GUI] mouseReleased: $position, $button"))
        sendMessage("mouseReleased", position, button)
        return super.mouseReleased(mouseX, mouseY, button)
    }
}