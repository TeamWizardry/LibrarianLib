package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.messaging.MessageEncoder
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.util.text.ITextComponent

public abstract class FacadeContainerScreen<T: Container>(
    container: T,
    inventory: PlayerInventory,
    title: ITextComponent
): ContainerScreen<T>(container, inventory, title) {
    public val player: PlayerEntity = inventory.player

    private val messageEncoder = MessageEncoder(container.javaClass, container.windowId)

    /**
     * Sends a message to both the client and server containers. Most actions should be performed using this method,
     * which will lead to easier synchronization between the client and server containers.
     */
    public fun sendMessage(name: String, vararg arguments: Any?) {
        LibrarianLibFacadeModule.channel.sendToServer(messageEncoder.encode(name, arguments))
        messageEncoder.invoke(container, name, arguments)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    }
}