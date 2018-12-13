package com.teamwizardry.librarianlib.features.text

import com.teamwizardry.librarianlib.features.helpers.pos
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import it.unimi.dsi.fastutil.chars.Char2ObjectMap
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.vertex.VertexBuffer
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import kotlin.math.ceil
import kotlin.math.sqrt

class Font(val location: ResourceLocation, val monospace: Boolean = false) {
    val texture = ResourceLocation(location.namespace, location.path + "/font.png")
    val glyphs = Char2ObjectOpenHashMap<Glyph>()
    lateinit var missingGlyph: Glyph
        private set
    var emSize: Int = 0
        private set
    var descender: Int = 0
        private set

    init {
        fonts.add(WeakReference(this))
        load()
    }

    fun load() {
        glyphs.clear()

        val metricsResource = Minecraft().resourceManager.getResource(ResourceLocation(location.namespace, location.path + "/metrics.bin"))
        val bytes = metricsResource.inputStream.readBytes()
        val buffer = ByteBuffer.wrap(bytes)
        val glyphCount = (bytes.size - 2) / 6

        emSize = buffer.get().toInt()
        descender = buffer.get().toInt()

        val gridCells = ceil(sqrt(glyphCount.toDouble())).toInt()
        val glyphSize = 1.0/gridCells
        (0 until glyphCount).forEach {
            val g = Glyph(this, it, buffer.getInt().toChar(), buffer.get().toInt(), buffer.get().toInt())
            g.uvSize = glyphSize
            g.u = (it % gridCells) * glyphSize
            g.v = (it / gridCells) * glyphSize
            glyphs[g.codepoint] = g
        }
        if(monospace) {
            glyphs.forEach {
                it.value.advance = emSize
            }
        }
        missingGlyph = glyphs[0xFFFF.toChar()]
            ?: throw FontException("Invalid font at $location. Fonts must include a 'Missing Glyph' glyph at 0xFFFF")
    }

    operator fun get(char: Char): Glyph {
        return glyphs[char] ?: missingGlyph
    }

    companion object {
        internal fun register() {
            ClientRunnable.registerReloadHandler {
                val newList = ArrayList<WeakReference<Font>>()

                for (font in Font.fonts) {
                    font.get()?.load()
                    if (font.get() != null) newList.add(font)
                }

                Font.fonts = newList
            }
        }

        var fonts: MutableList<WeakReference<Font>> = ArrayList()

        val tiny = Font("librarianlib:font/mctiny".toRl())
        val tinyMono = Font("librarianlib:font/mctiny".toRl(), true)
    }
}

class Glyph(val font: Font, val index: Int, val codepoint: Char, var advance: Int, val leftHang: Int) {
    var u: Double = 0.0
    var v: Double = 0.0
    var uvSize: Double = 0.0

    fun place(vb: BufferBuilder, color: Color, x: Int, y: Int, size: Int) {
        val left = x - leftHang * size
        val right = x + (font.emSize - leftHang) * size
        val top = y - (font.emSize - font.descender) * size
        val bottom = y + font.descender * size

        vb.pos(left,  top,    0).tex(u,        v       ).color(color).endVertex()
        vb.pos(right, top,    0).tex(u+uvSize, v       ).color(color).endVertex()
        vb.pos(right, bottom, 0).tex(u+uvSize, v+uvSize).color(color).endVertex()
        vb.pos(left,  bottom, 0).tex(u,        v+uvSize).color(color).endVertex()
    }
}

class FontException: RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
    constructor(message: String?, cause: Throwable?): super(message, cause)
    constructor(cause: Throwable?): super(cause)
}