package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibLibModule
import com.teamwizardry.librarianlib.courier.CourierClientPlayNetworking
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import com.teamwizardry.librarianlib.scaffold.messaging.MessageHandler
import com.teamwizardry.librarianlib.scaffold.messaging.MessagePacketType
import com.teamwizardry.librarianlib.scaffold.messaging.MessageSide
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.StencilUtil
import com.teamwizardry.librarianlib.facade.text.Fonts
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.SimpleTextContainer
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

internal object LibLibScaffold : LibLibModule("liblib_scaffold", "Scaffold") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibScaffold.makeLogger<CommonInitializer>()

        override fun onInitialize() {
            CourierServerPlayNetworking.registerGlobalReceiver(MessagePacketType) { packet, context ->
                context.execute {
                    packet.side = MessageSide.SERVER
                    (context.player.currentScreenHandler as? MessageHandler)?.receiveMessage(packet)
                }
            }
        }
    }

    object ClientInitializer : ClientModInitializer {
        private val logger = LibLibScaffold.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            CourierClientPlayNetworking.registerGlobalReceiver(MessagePacketType) { packet, context ->
                context.execute {
                    packet.side = MessageSide.CLIENT
                    (context.client.player?.currentScreenHandler as? MessageHandler)?.receiveMessage(packet)
                }
            }
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibScaffold.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
