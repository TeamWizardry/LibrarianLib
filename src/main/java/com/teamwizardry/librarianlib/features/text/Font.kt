package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import games.thecodewarrior.bitfont.data.Bitfont
import net.minecraft.util.ResourceLocation

object Fonts {
    lateinit var MCClassic: Bitfont
    lateinit var MCBitfont: Bitfont
    lateinit var MCBitfontBold: Bitfont
    lateinit var Unifont: Bitfont

    init {
        LibrarianLib.PROXY.addReloadHandler(ClientRunnable {
            reload()
        })

        reload()
    }

    fun reload() {
        MCClassic = load("librarianlib:font/mcclassicplus.bitfont".toRl())
        MCBitfont = load("librarianlib:font/mcbitfont.bitfont".toRl())
        MCBitfontBold = load("librarianlib:font/mcbitfontbold.bitfont".toRl())
        Unifont = load("librarianlib:font/unifont.bitfont".toRl())
    }

    fun load(fontLocation: ResourceLocation): Bitfont {
        try {
            val bytes = Minecraft().resourceManager.getResource(fontLocation).inputStream.readBytes()
            return Bitfont.unpack(bytes)
        } catch(e: Exception) {
            RuntimeException("Error loading $fontLocation", e).printStackTrace()
            return Bitfont("<err>", 10, 4, 9, 6, 2)
        }
    }
}
