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

object BitfontAtlas {
    var width: Int = 128
        private set
    var height: Int = 128
        private set

    private var texture = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    private var textureDirty = true
    private val texID = TextureUtil.glGenTextures()
    private val gpuMaxTexSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE)

    private var packer = RectanglePacker<BitGrid>(width, height, 1)
    private val rects = mutableMapOf<BitGrid, RectanglePacker.Rectangle>()
    private var solidRect = packer.insert(1, 1, BitGrid(1, 2).also { it[0, 0] = true })!!

    init {
        MinecraftForge.EVENT_BUS.register(this)
        TextureUtil.uploadTextureImage(texID, texture)
    }

    fun bind() {
        GlStateManager.bindTexture(texID)
    }

    fun solidTex(): Rect2d {
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(solidRect.x/width, solidRect.y/height, solidRect.width/width, solidRect.height/height)
    }

    fun rectFor(image: BitGrid): Rect2d {
        var rect = rects[image]
        if(rect == null) {
            insert(image)
            rect = rects[image] ?: throw IllegalStateException()
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
        val bufferedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_ARGB)
        for(x in 0 until image.width) {
            for(y in 0 until image.height) {
                if(image[x, y]) {
                    bufferedImage.setRGB(x, y, Color.WHITE.rgb)
                }
            }
        }

        TextureUtil.uploadTextureImageSub(texID, bufferedImage, xOrigin, yOrigin, false, false)
    }

    fun expand() {
        if(width == gpuMaxTexSize && height == gpuMaxTexSize)
            throw IllegalStateException("Ran out of atlas space! OpenGL max texture size: " +
                "$gpuMaxTexSize x $gpuMaxTexSize and managed to fit ${rects.size} glyphs.")
        width *= 2
        height *= 2
        packer.expand(width, height)
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        newImage.createGraphics().drawImage(texture, 0, 0, null)
        texture = newImage
        TextureUtil.uploadTextureImage(texID, texture)
    }

}