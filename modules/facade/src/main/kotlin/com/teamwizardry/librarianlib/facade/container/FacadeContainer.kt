package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import com.teamwizardry.librarianlib.facade.container.messaging.MessageDecoder
import com.teamwizardry.librarianlib.facade.container.messaging.MessageEncoder
import com.teamwizardry.librarianlib.facade.container.messaging.MessageHandler
import com.teamwizardry.librarianlib.facade.container.messaging.MessagePacket
import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import com.teamwizardry.librarianlib.facade.container.transfer.BasicTransferRule
import com.teamwizardry.librarianlib.facade.container.transfer.TransferManager
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
): Container(type, windowId), MessageHandler {
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
     * Send a message to the opposite side's container. The server should message the client to update state, and the
     * client screen should message the server when the player performs an action. Which side this container is on can
     * be checked using [isClientContainer].
     */
    public fun sendMessage(name: String, vararg arguments: Any?) {
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

    public fun createTransferRule(): BasicTransferRule {
        return transferManager.createBasicRule()
    }

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        return transferManager.transferStackInSlot(inventorySlots[index])
    }

    override fun slotClick(slotId: Int, dragType: Int, clickTypeIn: ClickType, player: PlayerEntity): ItemStack {
        val customClickResult = (inventorySlots.getOrNull(slotId) as? CustomClickSlot?)?.handleClick(this, dragType, clickTypeIn, player)
        if (customClickResult != null) {
            return customClickResult
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player)
    }
}
