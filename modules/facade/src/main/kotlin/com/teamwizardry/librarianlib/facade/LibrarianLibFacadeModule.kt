package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibrarianLibModule
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.loc
import com.teamwizardry.librarianlib.courier.CourierChannel
import com.teamwizardry.librarianlib.facade.layer.supporting.StencilUtil
import com.teamwizardry.librarianlib.facade.container.messaging.MessagePacketType
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.Fonts
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.TextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

internal object LibrarianLibFacadeModule: LibrarianLibModule("facade", "Facade") {
    val channel: CourierChannel = CourierChannel(loc("librarianlib", "facade"), "0")

    init {
        channel.register(MessagePacketType)
    }

    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        Client.runAsync {
            StencilUtil.enableStencilBuffer()
            preload()
        }
    }

    /**
     * The first time a screen opens a lot of stuff needs to load, and that can take a second or two. Waiting a second
     * or two for a GUI to load the first time is absolutely unacceptable, so we have to preload some of the resources.
     */
    @OnlyIn(Dist.CLIENT)
    fun preload() {
        /*
         * For some reason finding (not loading, just locating) classes/textures takes a long time. I did some
         * profiling and it took 115 milliseconds for only 32 cursor ResourceLocations to load, and the GuiLayer class
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

internal val logger = LibrarianLibFacadeModule.makeLogger(null)
