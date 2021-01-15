package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import com.teamwizardry.librarianlib.facade.container.messaging.*
import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import com.teamwizardry.librarianlib.facade.container.transfer.BasicTransferRule
import com.teamwizardry.librarianlib.facade.container.transfer.TransferManager
import com.teamwizardry.librarianlib.facade.container.transfer.TransferRule
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.network.PacketDistributor

public abstract class FacadeContainer(
    type: ContainerType<*>,
    windowId: Int,
    public val player: PlayerEntity
) : Container(type, windowId), MessageHandler {
    public val isClientContainer: Boolean = player !is ServerPlayerEntity

    public val transferManager: TransferManager = TransferManager()
    public val playerSlots: PlayerInventorySlotManager = PlayerInventorySlotManager(player.inventory)

    private val messageDirection: PacketDistributor.PacketTarget = if (player is ServerPlayerEntity)
        PacketDistributor.PLAYER.with { player }
    else
        PacketDistributor.SERVER.noArg()

    private val encoder = MessageEncoder(javaClass, windowId)
    private val decoder = MessageDecoder(this, windowId)

    /**
     * Send a message to both this container and the opposite side's container. Which side this container is on can be
     * checked using [isClientContainer].
     */
    protected fun sendMessage(name: String, vararg arguments: Any?) {
        LibrarianLibFacadeModule.channel.send(messageDirection, encoder.encode(name, arguments))
        encoder.invoke(this, name, arguments)
    }

    /**
     * Send a message to the client container, or print a warning if called on the client. Which side this container is
     * on can be checked using [isClientContainer].
     */
    protected fun sendClientMessage(name: String, vararg arguments: Any?) {
        if (isClientContainer) {
            logger.warn("Tried to send a client message '$name' from the client.", RuntimeException())
            return
        }
        LibrarianLibFacadeModule.channel.send(messageDirection, encoder.encode(name, arguments))
    }

    /**
     * Send a message to the server container, or print a warning if called on the server. Which side this container is
     * on can be checked using [isClientContainer].
     */
    protected fun sendServerMessage(name: String, vararg arguments: Any?) {
        if (!isClientContainer) {
            logger.warn("Tried to send a server message '$name' from the server.", RuntimeException())
            return
        }
        LibrarianLibFacadeModule.channel.send(messageDirection, encoder.encode(name, arguments))
    }

    override fun receiveMessage(packet: MessagePacket) {
        decoder.execute(packet)
    }

    public fun addSlots(region: SlotRegion) {
        region.forEach {
            addSlot(it)
        }
    }

    public fun <T : TransferRule> addTransferRule(rule: T): T {
        transferManager.add(rule)
        return rule
    }

    public fun createTransferRule(): BasicTransferRule {
        return transferManager.createBasicRule()
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        return transferManager.transferStackInSlot(inventorySlots[index])
    }

    override fun slotClick(slotId: Int, mouseButton: Int, clickTypeIn: ClickType, player: PlayerEntity): ItemStack {
        val customClickResult =
            (inventorySlots.getOrNull(slotId) as? CustomClickSlot?)?.handleClick(this, mouseButton, clickTypeIn, player)
        if (customClickResult != null) {
            return customClickResult
        }
        return super.slotClick(slotId, mouseButton, clickTypeIn, player)
    }

    @Message
    private fun acceptJeiGhostStack(slotNumber: Int, stack: ItemStack) {
        val slot = inventorySlots[slotNumber]
        if(slot is GhostSlot && !slot.disableJeiGhostIntegration)
            slot.acceptJeiGhostStack(stack)
    }

    public companion object {
        private val logger = LibrarianLibFacadeModule.makeLogger<FacadeContainer>()
    }
}
