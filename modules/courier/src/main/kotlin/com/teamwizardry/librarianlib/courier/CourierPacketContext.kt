package com.teamwizardry.librarianlib.courier

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity

/**
 *
 */
public sealed class CourierPacketContext {
    public data class Client(
        val client: MinecraftClient,
        val handler: ClientPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    ) : CourierPacketContext()

    public data class Server(
        val server: MinecraftServer,
        val player: ServerPlayerEntity,
        val handler: ServerPlayNetworkHandler,
        val buf: PacketByteBuf,
        val responseSender: PacketSender
    ) : CourierPacketContext()
}
