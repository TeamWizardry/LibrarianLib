package com.teamwizardry.librarianlib.courier

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler

/**
 * The courier counterpart to [ClientPlayNetworking].
 */
@Environment(EnvType.CLIENT)
public object CourierClientPlayNetworking {
    /**
     * Registers a handler to a channel.
     * A global receiver is registered to all connections, in the present and future.
     *
     * The given packet type specifies the channel identifier and decodes the packet.
     *
     * If a handler is already registered to the channel, this method will return false, and no change will be made.
     * Use [ClientPlayNetworking.unregisterReceiver] using the type's [channel][PacketType.channel] to unregister the
     * existing handler.
     *
     * @param type the packet type. This specifies the channel identifier and decodes the packet
     * @param packetHandler the handler for incoming packets
     * @return false if a handler is already registered to the channel
     * @see ClientPlayNetworking.unregisterGlobalReceiver
     * @see ClientPlayNetworking.registerReceiver
     */
    @JvmStatic
    public fun <T : Any> registerGlobalReceiver(
        type: PacketType<T>,
        packetHandler: CourierPacketHandler<T>
    ): Boolean {
        return ClientPlayNetworking.registerGlobalReceiver(type.channel, createChannelHandler(type, packetHandler))
    }

    /**
     * Sends a packet to the connected server.
     *
     * @param type the packet type. This specifies the channel and encodes the packet
     * @param packet the packet to send
     * @throws IllegalStateException if the client is not connected to a server
     */
    @Throws(IllegalStateException::class)
    public fun <T : Any> send(type: PacketType<T>, packet: T) {
        val buffer = CourierBuffer.create()
        type.encode(packet, buffer)
        ClientPlayNetworking.send(type.channel, buffer)
    }

    @JvmStatic
    public fun <T : Any> createChannelHandler(
        type: PacketType<T>,
        packetHandler: CourierPacketHandler<T>
    ): ClientPlayNetworking.PlayChannelHandler {
        return ClientPlayNetworking.PlayChannelHandler { client, handler, buf, responseSender ->
            val packet = type.decode(CourierBuffer(buf))
            packetHandler.receive(client, handler, packet, responseSender)
        }
    }

    @Environment(EnvType.CLIENT)
    public fun interface CourierPacketHandler<T : Any> {
        /**
         * Handles an incoming packet.
         *
         * This method is executed on [netty’s event loops][io.netty.channel.EventLoop].
         * Modification to the game should be [scheduled][net.minecraft.util.thread.ThreadExecutor.submit] using the
         * provided Minecraft client instance.
         *
         *
         * An example usage of this is to display an overlay message:
         * ```
         * CourierClientPlayNetworking.registerReceiver(ModPacketTypes.OVERLAY, (client, handler, packet, responseSender) -> {
         *     // All operations on the server or world must be executed on the server thread
         *     client.execute(() -> {
         *         client.inGameHud.setOverlayMessage(packet.message, true);
         *     });
         * });
         * ```
         *
         * @param client the client
         * @param handler the network handler that received this packet
         * @param packet the received packet
         * @param responseSender the packet sender
         */
        public fun receive(
            client: MinecraftClient,
            handler: ClientPlayNetworkHandler,
            packet: T,
            responseSender: PacketSender
        )
    }
}