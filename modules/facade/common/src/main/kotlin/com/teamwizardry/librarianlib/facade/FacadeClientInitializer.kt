package com.teamwizardry.librarianlib.facade

import com.teamwizardry.librarianlib.LibLibInternal
import com.teamwizardry.librarianlib.facade.input.Cursor
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.text.Fonts
import com.teamwizardry.librarianlib.platform.LibLibPlatformCommon
import dev.thecodewarrior.bitfont.typesetting.AttributedString
import dev.thecodewarrior.bitfont.typesetting.SimpleTextContainer
import dev.thecodewarrior.bitfont.typesetting.TextLayoutManager
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier

@LibLibInternal
public object FacadeClientInitializer {
    public fun initializeClient() {
        LibLibPlatformCommon.instance.registerResourceReloadListener(
            ResourceType.CLIENT_RESOURCES,
            Fonts,
            Identifier.of("liblib_facade:fonts")
        )
        LibLibPlatformCommon.instance.registerResourceReloadListener(
            ResourceType.CLIENT_RESOURCES,
            Cursor,
            Identifier.of("liblib_facade:cursor")
        )
    }

    /**
     * The first time a screen opens a lot of stuff needs to load, and that can take a second or two. Waiting a second
     * or two for a GUI to load the first time is unacceptable, so we have to preload some of the resources.
     */
    private fun preload() {
        /*
         * TODO: Test if this is still true in quilt
         *
         * For some reason finding (not loading, just locating) classes/textures takes a long time. I did some
         * profiling and it took 115 milliseconds for only 32 cursor Identifiers to load, and the GuiLayer class
         * ends up loading ~200-300 other classes, so it suffers the same issue.
         *
         * In total, this can easily cause a 1-2 second hitch when first opening a Facade GUI, which is unacceptable,
         * so unfortunately it's necessary to preload them. I don't like it, I don't feel like I should have to, but I do.
         */
        GuiLayer()
        Cursor

        // Fonts can take a bit to load (around about 250-500ms IIRC)
        Fonts

        // The text layout manager loads some ICU stuff (e.g. break iterators and combining classes)
        val manager = TextLayoutManager(Fonts.classic, SimpleTextContainer(10, 50))
        manager.attributedString = AttributedString("x x x x")
        manager.layoutText()
    }
}
