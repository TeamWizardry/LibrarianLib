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
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.util.TreeMap
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class BitfontAtlas private constructor() {
    var width: Int = 128
        private set
    var height: Int = 128
        private set
    private var texture = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private var textureDirty = true
    private val texID = TextureUtil.glGenTextures()

    private var packer = RectanglePacker<BitGrid>(width, height, 0)
    private val rects = mutableMapOf<BitGrid, RectanglePacker.Rectangle>()
    private var solidRect = packer.insert(1, 1, BitGrid(1, 2).also { it[0, 0] = true })!!

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun bind() {
        GlStateManager.bindTexture(texID)
    }

    fun solidTex(): Rect2d {
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(solidRect.x/width, solidRect.y/height, solidRect.width/width, solidRect.height/height)
    }

    fun rectFor(image: BitGrid): Rect2d? {
        var rect = rects[image]
        if(rect == null) {
            insert(image)
            rect = rects[image] ?: return null
        }
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(rect.x/width, rect.y/height, rect.width/width, rect.height/height)
    }

    fun load(images: List<BitGrid>) {
        images.forEach(::insert)
    }

    fun insert(image: BitGrid) {
        var newRect: RectanglePacker.Rectangle? = packer.insert(image.width, image.height, image)
        if(newRect == null) {
            expand()
            newRect = packer.insert(image.width, image.height, image) ?: return
        }
        rects[image] = newRect
        draw(image, newRect.x, newRect.y)
    }

    fun draw(image: BitGrid, xOrigin: Int, yOrigin: Int) {
        for(x in 0 until image.width) {
            for(y in 0 until image.height) {
                if(image[x, y]) {
                    texture.setRGB(xOrigin+x, yOrigin+y, Color.WHITE.rgb)
                }
            }
        }
        textureDirty = true
    }

    fun expand() {
        if(width == gpuMaxTexSize && height == gpuMaxTexSize) return
        width = min(ceil(width*1.5).toInt(), gpuMaxTexSize)
        height = min(ceil(height*1.5).toInt(), gpuMaxTexSize)
        packer.expand(width, height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        newImage.createGraphics().drawImage(texture, 0, 0, null)
        texture = newImage
        textureDirty = true
    }

    @SubscribeEvent
    fun renderTickEnd(e: TickEvent.RenderTickEvent) {
        if(e.phase != TickEvent.Phase.END) return
        if(!textureDirty) return
        textureDirty = false

        TextureUtil.uploadTextureImage(texID, texture)
    }

    companion object {
        val gpuMaxTexSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)

        private val atlases = mutableListOf(BitfontAtlas())
        operator fun get(image: BitGrid): Pair<BitfontAtlas, Rect2d> {
            for(i in 0 .. atlases.size) {
                if(i == atlases.size)
                    atlases.add(BitfontAtlas())
                val rect = atlases[i].rectFor(image) ?: continue
                return atlases[i] to rect
            }
            return atlases[0] to atlases[0].solidTex()
        }
    }
}