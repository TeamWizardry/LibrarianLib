package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import games.thecodewarrior.bitfont.data.Bitfont
import it.unimi.dsi.fastutil.chars.Char2ObjectMap
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.client.resources.IResourceManagerReloadListener
import net.minecraft.util.ResourceLocation
import org.msgpack.core.MessagePackException
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import kotlin.math.ceil
import kotlin.math.sqrt

object Fonts {
    lateinit var MCClassic: Bitfont
    lateinit var MCBitfont: Bitfont

    init {
        LibrarianLib.PROXY.addReloadHandler(ClientRunnable {
            reload()
        })

        reload()
    }

    fun reload() {
        MCClassic = load("librarianlib:font/MCClassic.bitfont".toRl())
        MCBitfont = load("librarianlib:font/MCBitfont.bitfont".toRl())
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
