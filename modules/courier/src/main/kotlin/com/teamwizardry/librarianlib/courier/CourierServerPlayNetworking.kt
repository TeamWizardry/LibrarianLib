package com.teamwizardry.librarianlib.courier

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

/**
 * The courier counterpart to [ServerPlayNetworking].
 */
public object CourierServerPlayNetworking {
    /**
     * Registers a handler to a channel.
     * A global receiver is registered to all connections, in the present and future.
     *
     * The given packet type specifies the channel identifier and decodes the packet.
     *
     * If a handler is already registered to the channel, this method will return false, and no change will be made.
     * Use [ServerPlayNetworking.unregisterReceiver] using the type's [channel][PacketType.channel] to unregister the
     * existing handler.
     *
     * @param type the packet type. This specifies the channel identifier and decodes the packet
     * @param packetHandler the handler for incoming packets
     * @return false if a handler is already registered to the channel
     * @see ServerPlayNetworking.registerGlobalReceiver
     */
    @JvmStatic
    public fun <T : Any> registerGlobalReceiver(
        type: PacketType<T>,
        packetHandler: CourierPacketHandler<T>
    ): Boolean {
        return ServerPlayNetworking.registerGlobalReceiver(type.channel, createChannelHandler(type, packetHandler))
    }

    /**
     * Sends a packet to a player.
     *
     * @param player the player to send the packet to
     * @param type the packet type to send. This specifies the channel identifier and encodes the packet
     * @param packet the packet to send
     */
    @JvmStatic
    public fun <T : Any> send(player: ServerPlayerEntity, type: PacketType<T>, packet: T) {
        val buffer = CourierBuffer.create()
        type.encode(packet, buffer)
        ServerPlayNetworking.send(player, type.channel, buffer)
    }

    @JvmStatic
    public fun <T : Any> createChannelHandler(
        type: PacketType<T>,
        packetHandler: CourierPacketHandler<T>
    ): ServerPlayNetworking.PlayChannelHandler {
        return ServerPlayNetworking.PlayChannelHandler { server, player, handler, buf, responseSender ->
            val packet = type.decode(CourierBuffer(buf))
            packetHandler.receive(packet, PacketContext(server, player, handler, responseSender))
        }
    }

    public fun interface CourierPacketHandler<T : Any> {
        /**
         * Handles an incoming packet.
         *
         * This method is executed on [netty’s event loops][io.netty.channel.EventLoop].
         * Modification to the game should be [scheduled][net.minecraft.util.thread.ThreadExecutor.submit] using the
         * provided context.
         *
         * An example usage of this is to create an explosion where the player is looking:
         * ```java
         * CourierServerPlayNetworking.registerReceiver(ModPacketTypes.BOOM, (packet, context) -> {
         *     // All operations on the server or world must be executed on the server thread
         *     context.execute(() -> {
         *         ModPacketHandler.createExplosion(context.player, packet.fire);
         *     });
         * });
         * ```
         */
        public fun receive(
            packet: T,
            context: PacketContext
        )
    }

    public class PacketContext(
        /**
         * the server
         */
        public val server: MinecraftServer,
        /**
         * the player
         */
        public val player: ServerPlayerEntity,
        /**
         * the network handler that received this packet, representing the player/client who sent the packet
         */
        public val handler: ServerPlayNetworkHandler,
        /**
         * the packet sender
         */
        public val responseSender: PacketSender
    ) {
        public fun execute(runnable: Runnable) {
            server.execute(runnable)
        }
    }
}