package com.teamwizardry.librarianlib.features.text

import com.ibm.icu.lang.UCharacter
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.math.Rect2d
import games.thecodewarrior.bitfont.data.BitGrid
import games.thecodewarrior.bitfont.data.Bitfont
import games.thecodewarrior.bitfont.data.Glyph
import games.thecodewarrior.bitfont.utils.RectanglePacker
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.util.TreeMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class BitfontAtlas private constructor(val font: Bitfont) {
    var width: Int = 128
        private set
    var height: Int = 128
        private set
    private var image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private var textureDirty = true
    private val texID = TextureUtil.glGenTextures()

    private var packer = RectanglePacker<Int>(width, height, 0)
    private val rects = Int2ObjectOpenHashMap<RectanglePacker.Rectangle>()
    private var defaultRect = packer.insert(font.defaultGlyph.image.width, font.defaultGlyph.image.height, -1)!!
    private var solidRect = packer.insert(1, 1, -1)!!
    private val advanceMap = TreeMap<Int, MutableList<Int>>()

    init {
        MinecraftForge.EVENT_BUS.register(this)
        draw(font.defaultGlyph, defaultRect.x, defaultRect.y)
        draw(Glyph().also { it.image[0, 0] = true }, solidRect.x, solidRect.y)
        load(' '..'~')
    }

    fun bind() {
        GlStateManager.bindTexture(texID)
    }

    fun solidTex(): Rect2d {
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(solidRect.x/width, solidRect.y/height, solidRect.width/width, solidRect.height/height)
    }

    fun texCoords(codepoint: Int): Rect2d {
        if(codepoint !in rects) {
            insert(codepoint)
            if(codepoint !in rects) {
                rects[codepoint] = defaultRect
            }
        }
        val rect = rects[codepoint] ?: return rect(0, 0, 0, 0)
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(rect.x/width, rect.y/height, rect.width/width, rect.height/height)
    }

    fun obfTransform(codepoint: Int): Int {
        val advance = font.glyphs[codepoint].calcAdvance(font.spacing)
        return advanceMap.floorEntry(advance)?.value?.random() ?: codepoint
    }

    fun load(ints: IntRange) {
        ints.forEach(::insert)
    }
    fun load(chars: ClosedRange<Char>) {
        (chars.start.toInt() .. chars.endInclusive.toInt()).forEach(::insert)
    }
    fun load(string: String) {
        string.codePoints().forEach(::insert)
    }

    fun insert(codepoint: Int) {
        val glyph = font.glyphs[codepoint] ?: return
        var newRect: RectanglePacker.Rectangle? = packer.insert(glyph.image.width, glyph.image.height, codepoint)
        if(newRect == null) {
            expand()
            newRect = packer.insert(glyph.image.width, glyph.image.height, codepoint)
        }
        rects[codepoint] = newRect!!
        draw(glyph, newRect.x, newRect.y)
        if(!glyph.image.isEmpty())
            advanceMap.getOrPut(glyph.calcAdvance(font.spacing)) { mutableListOf() }.add(codepoint)
    }

    fun draw(glyph: Glyph, xOrigin: Int, yOrigin: Int) {
        for(x in 0 until glyph.image.width) {
            for(y in 0 until glyph.image.height) {
                if(glyph.image[x, y]) {
                    image.setRGB(xOrigin+x, yOrigin+y, Color.WHITE.rgb)
                }
            }
        }
        textureDirty = true
    }

    fun expand() {
        width = ceil(width*1.5).toInt()
        height = ceil(height*1.5).toInt()
        packer.expand(width, height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        newImage.createGraphics().drawImage(image, 0, 0, null)
        image = newImage
        textureDirty = true
    }

    @SubscribeEvent
    fun renderTickEnd(e: TickEvent.RenderTickEvent) {
        if(e.phase != TickEvent.Phase.END) return
        if(!textureDirty) return
        textureDirty = false

        TextureUtil.uploadTextureImage(texID, image)
    }

    companion object {
        private val map = mutableMapOf<Bitfont, BitfontAtlas>()
        operator fun get(font: Bitfont): BitfontAtlas {
            return map.getOrPut(font) { BitfontAtlas(font) }
        }
    }
}