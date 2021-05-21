package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibLibModule
import com.teamwizardry.librarianlib.courier.CourierClientPlayNetworking
import com.teamwizardry.librarianlib.courier.CourierServerPlayNetworking
import com.teamwizardry.librarianlib.facade.container.messaging.MessageHandler
import com.teamwizardry.librarianlib.facade.container.messaging.MessagePacketType
import com.teamwizardry.librarianlib.facade.container.messaging.MessageSide
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.supporting.StencilUtil
import com.teamwizardry.librarianlib.facade.text.Fonts
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resource.ResourceType

internal object LibLibFacade : LibLibModule("liblib-facade", "Facade") {
    object CommonInitializer : ModInitializer {
        private val logger = LibLibFacade.makeLogger<CommonInitializer>()

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
        private val logger = LibLibFacade.makeLogger<ClientInitializer>()

        override fun onInitializeClient() {
            CourierClientPlayNetworking.registerGlobalReceiver(MessagePacketType) { packet, context ->
                context.execute {
                    packet.side = MessageSide.CLIENT
                    (context.client.player?.currentScreenHandler as? MessageHandler)?.receiveMessage(packet)
                }
            }

            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(Cursor)
            preload()
        }

        /**
         * The first time a screen opens a lot of stuff needs to load, and that can take a second or two. Waiting a second
         * or two for a GUI to load the first time is absolutely unacceptable, so we have to preload some of the resources.
         *
         */
        fun preload() {
            /*
             * TODO: Test if this is still true in quilt
             *
             * For some reason finding (not loading, just locating) classes/textures takes a long time. I did some
             * profiling and it took 115 milliseconds for only 32 cursor Identifiers to load, and the GuiLayer class
             * ends up loading ~200-300 other classes, so it suffers the same issue.
             *
             * In total, this can easily cause a 1-2 second hitch when first opening a Facade GUI, which is beyond
             * unacceptable, so unfortunately it's necessary to preload them. I don't like it, I don't feel like I should
             * have to, but I do.
             */
            GuiLayer()
            Cursor

            // Fonts can take a bit to load (around about 250-500ms IIRC)
            Fonts

            // The text layout manager loads some ICU stuff (e.g. break iterators and combining classes)
            val manager = TextLayoutManager(Fonts.classic, TextContainer(10, 50))
            manager.attributedString = AttributedString("x x x x")
            manager.layoutText()
        }
    }

    object ServerInitializer : DedicatedServerModInitializer {
        private val logger = LibLibFacade.makeLogger<ServerInitializer>()

        override fun onInitializeServer() {
        }
    }
}
