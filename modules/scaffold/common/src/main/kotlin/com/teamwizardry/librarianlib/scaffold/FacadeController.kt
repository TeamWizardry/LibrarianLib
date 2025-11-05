package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.facade.LibLibFacade
import com.teamwizardry.librarianlib.facade.container.builtin.PlayerInventorySlotManager
import com.teamwizardry.librarianlib.facade.container.messaging.*
import com.teamwizardry.librarianlib.facade.container.slot.CustomClickSlot
import com.teamwizardry.librarianlib.facade.container.slot.SlotRegion
import com.teamwizardry.librarianlib.facade.container.transfer.BasicTransferRule
import com.teamwizardry.librarianlib.facade.container.transfer.TransferManager
import com.teamwizardry.librarianlib.facade.container.transfer.TransferRule
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import net.minecraft.util.registry.Registry

public abstract class FacadeController(
    type: ScreenHandlerType<*>,
    windowId: Int,
    public val player: PlayerEntity
) : ScreenHandler(type, windowId), MessageHandler {
    public constructor(
        type: FacadeControllerType<*>,
        windowId: Int,
        player: PlayerEntity
    ) : this(type.screenHandlerType, windowId, player)

    public val isClientContainer: Boolean = player !is ServerPlayerEntity

    public val transferManager: TransferManager = TransferManager()
    public val playerSlots: PlayerInventorySlotManager = PlayerInventorySlotManager(player.inventory)

    private val messageSender: MessageSender =
        if (player is ServerPlayerEntity)
            MessageSender.ServerToClient(player)
        else
            MessageSender.ClientToServer

    private val encoder = MessageEncoder(javaClass, windowId)
    private val decoder = MessageDecoder(this, windowId)

    /**
     * Send a message to both this container and the opposite side's container. Which side this container is on can be
     * checked using [isClientContainer].
     */
    protected fun sendMessage(name: String, vararg arguments: Any?) {
        messageSender.send(encoder.encode(name, arguments))
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
        messageSender.send(encoder.encode(name, arguments))
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
        messageSender.send(encoder.encode(name, arguments))
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

    override fun transferSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        return transferManager.transferStackInSlot(slots[index])
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        try {
            if((slots.getOrNull(slotIndex) as? CustomClickSlot?)?.handleClick(this, button, actionType, player) == true) {
                return
            }
        } catch (var8: Exception) {
            val crashReport = CrashReport.create(var8, "Container click")
            val crashReportSection = crashReport.addElement("Click info")
            crashReportSection.add("Menu Type") {
                if (type != null) Registry.SCREEN_HANDLER.getId(type).toString() else "<no type>"
            }
            crashReportSection.add("Menu Class") { this.javaClass.canonicalName }
            crashReportSection.add("Slot Count", slots.size as Any)
            crashReportSection.add("Slot", slotIndex as Any?)
            crashReportSection.add("Button", button as Any?)
            crashReportSection.add("Type", actionType as Any?)
            throw CrashException(crashReport)
        }
        return super.onSlotClick(slotIndex, button, actionType, player)
    }

    public companion object {
        private val logger = LibLibFacade.makeLogger<FacadeController>()
    }
}
