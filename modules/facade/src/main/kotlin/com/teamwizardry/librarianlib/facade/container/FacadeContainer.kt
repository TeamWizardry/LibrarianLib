package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.core.util.kotlin.unmodifiableView
import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.builtin.GhostSlot
import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import com.teamwizardry.librarianlib.facade.container.messaging.*
import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.FluidSlot
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
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.network.PacketDistributor

public abstract class FacadeContainer(
    type: ScreenHandlerType<*>,
    windowId: Int,
    public val player: PlayerEntity
) : ScreenHandler(type, windowId), MessageHandler {
    public val isClientContainer: Boolean = player !is ServerPlayerEntity

    public val transferManager: TransferManager = TransferManager()
    public val playerSlots: PlayerInventorySlotManager = PlayerInventorySlotManager(player.inventory)

    private val messageDirection: PacketDistributor.PacketTarget = if (player is ServerPlayerEntity)
        PacketDistributor.PLAYER.with { player }
    else
        PacketDistributor.SERVER.noArg()

    private val _fluidSlots: MutableList<FluidSlot> = mutableListOf()
    public val fluidSlots: List<FluidSlot> = _fluidSlots.unmodifiableView()

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

    public fun addSlot(fluidSlot: FluidSlot) {
        fluidSlot.slotNumber = _fluidSlots.size
        _fluidSlots.add(fluidSlot)
    }

    public fun <T : TransferRule> addTransferRule(rule: T): T {
        transferManager.add(rule)
        return rule
    }

    public fun createTransferRule(): BasicTransferRule {
        return transferManager.createBasicRule()
    }

    override fun transferSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        return transferManager.transferStackInSlot(slots[index])
    }

    override fun onSlotClick(slotId: Int, mouseButton: Int, clickTypeIn: SlotActionType, player: PlayerEntity): ItemStack {
        val customClickResult =
            (slots.getOrNull(slotId) as? CustomClickSlot?)?.handleClick(this, mouseButton, clickTypeIn, player)
        if (customClickResult != null) {
            return customClickResult
        }
        return super.onSlotClick(slotId, mouseButton, clickTypeIn, player)
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()

        for(i in fluidSlots.indices) {
            val slot = fluidSlots[i]
            val actual = slot.getActualFluid()
            if(!actual.isFluidStackIdentical(slot.fluid)) {
                slot.setCachedContents(actual.copy())
                sendClientMessage("syncFluidSlot", slot.slotNumber, slot.fluid)
            }
        }
    }

    @Message(side = MessageSide.CLIENT)
    private fun syncFluidSlot(slotNumber: Int, stack: FluidStack) {
        fluidSlots[slotNumber].setCachedContents(stack)
    }

    @Message
    private fun acceptJeiGhostStack(slotNumber: Int, stack: ItemStack) {
        val slot = slots[slotNumber]
        if(slot is GhostSlot && !slot.disableJeiGhostIntegration)
            slot.acceptJeiGhostStack(stack)
    }

    public companion object {
        private val logger = LibrarianLibFacadeModule.makeLogger<FacadeContainer>()
    }
}
