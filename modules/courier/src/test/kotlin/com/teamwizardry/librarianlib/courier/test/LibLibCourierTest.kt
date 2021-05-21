package com.teamwizardry.librarianlib.courier.test

import com.teamwizardry.librarianlib.core.util.ModLogManager
import com.teamwizardry.librarianlib.courier.CourierClientPlayNetworking
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import com.teamwizardry.librarianlib.testcore.TestModContentManager
import com.teamwizardry.librarianlib.testcore.content.TestItem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText

internal object LibLibCourierTest {
    val logManager: ModLogManager = ModLogManager("liblib-courier-test", "LibrarianLib Courier Test")
    val manager: TestModContentManager = TestModContentManager("liblib-courier-test", "Courier", logManager)

    object CommonInitializer : ModInitializer {
        private val logger = logManager.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            manager.create<TestItem>("server_to_client") {
                name = "Server to client packet"

                rightClick.server {
                    val packet = TestPacket(Blocks.DIRT, 42)
                    packet.manual = 100
                    player.sendMessage(LiteralText("Server sending: $packet (manual = ${packet.manual})"), false)
                    CourierServerPlayNetworking.send(player as ServerPlayerEntity, TestPacketTypes.testPacket, packet)
                }
            }
            manager.registerCommon()
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = logManager.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            CourierClientPlayNetworking.registerGlobalReceiver(TestPacketTypes.testPacket) { packet, context ->
                context.execute {
                    context.client.player?.sendMessage(LiteralText("CourierPacket handler: $packet (manual = ${packet.manual})"), false)
                }
            }
            manager.registerClient()
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = logManager.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
            manager.registerServer()
        }
    }
}
