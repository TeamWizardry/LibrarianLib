package com.teamwizardry.librarianlib.facade.container.messaging

import com.teamwizardry.librarianlib.courier.CourierClientPlayNetworking
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity

public sealed class MessageSender {
    public abstract fun send(message: MessagePacket)

    public object ClientToServer : MessageSender() {
        override fun send(message: MessagePacket) {
            CourierClientPlayNetworking.send(MessagePacketType, message)
        }
    }

    public class ServerToClient(public val player: ServerPlayerEntity) : MessageSender() {
        override fun send(message: MessagePacket) {
            CourierServerPlayNetworking.send(player, MessagePacketType, message)
        }
    }
}
