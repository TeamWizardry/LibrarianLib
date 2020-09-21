package com.teamwizardry.librarianlib.facade.container

import com.teamwizardry.librarianlib.facade.LibrarianLibFacadeModule
import com.teamwizardry.librarianlib.facade.container.messaging.MessageDecoder
import com.teamwizardry.librarianlib.facade.container.messaging.MessageEncoder
import com.teamwizardry.librarianlib.facade.container.messaging.MessageHandler
import com.teamwizardry.librarianlib.facade.container.messaging.MessagePacket
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraftforge.fml.network.PacketDistributor

public abstract class FacadeContainer(
    type: ContainerType<*>,
    windowId: Int,
    public val player: PlayerEntity
): Container(type, windowId), MessageHandler {
    public val isClientContainer: Boolean = player !is ServerPlayerEntity

    private val messageDirection: PacketDistributor.PacketTarget = if(player is ServerPlayerEntity)
        PacketDistributor.PLAYER.with { player }
    else
        PacketDistributor.SERVER.noArg()

    private val encoder = MessageEncoder(javaClass, windowId)
    private val decoder = MessageDecoder(this, windowId)

    /**
     * Send a message to the opposite side's container. Often the server will be the one messaging the client, not the
     * other way around. Which side this container is on can be checked using [isClientContainer].
     */
    public fun sendMessage(name: String, vararg arguments: Any?) {
        LibrarianLibFacadeModule.channel.send(messageDirection, encoder.encode(name, arguments))
    }

    override fun receiveMessage(packet: MessagePacket) { decoder.execute(packet) }
}
