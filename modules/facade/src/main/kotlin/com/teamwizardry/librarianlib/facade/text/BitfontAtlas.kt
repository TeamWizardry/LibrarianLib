package com.teamwizardry.librarianlib.facade.text

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Rect2d
import com.teamwizardry.librarianlib.math.rect
import dev.thecodewarrior.bitfont.data.BitGrid
import dev.thecodewarrior.bitfont.utils.RectanglePacker
import net.minecraft.client.renderer.texture.NativeImage
import net.minecraft.client.renderer.texture.Texture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.concurrent.Executor

object BitfontAtlas: Texture() {
    val ATLAS_LOCATION: ResourceLocation = ResourceLocation("librarianlib:textures/atlas/bitfont.png")

    var width: Int = 128
        private set
    var height: Int = 128
        private set

    private val gpuMaxTexSize = GlStateManager.getInteger(GL11.GL_MAX_TEXTURE_SIZE)

    private var packer = RectanglePacker<BitGrid>(width, height, 1)
    private val rects = mutableMapOf<BitGrid, RectanglePacker.Rectangle>()
    private var solidRect: Rect2d = Rect2d.ZERO

    init {
        Client.textureManager.loadTexture(ATLAS_LOCATION, this)
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
        images.forEach { insert(it) }
    }

    fun insert(image: BitGrid) {
        if(image in rects)
            return
        var newRect: RectanglePacker.Rectangle? = packer.insert(image.width, image.height, image)
        if(newRect == null) {
            expand()
            newRect = packer.insert(image.width, image.height, image) ?: return
        }
        rects[image] = newRect
        draw(image, newRect.x, newRect.y)
    }

    private fun draw(image: BitGrid, xOrigin: Int, yOrigin: Int) {
        val native = NativeImage(image.width, image.height, true)
        for(x in 0 until image.width) {
            for(y in 0 until image.height) {
                if(image[x, y]) {
                    native.setPixelRGBA(x, y, 0.inv())
                }
            }
        }

        this.bindTexture()
        native.uploadTextureSub(0, xOrigin, yOrigin, false)
        native.close()
    }

    private fun expand() {
        width *= 2
        height *= 2
        if(width > gpuMaxTexSize || height > gpuMaxTexSize)
            throw IllegalStateException("Ran out of atlas space! OpenGL max texture size: " +
                "$gpuMaxTexSize x $gpuMaxTexSize and managed to fit ${rects.size} glyphs.")
        packer.expand(width, height)
        TextureUtil.prepareImage(glTextureId, width, height)
        rects.forEach { (image, rect) ->
            draw(image, rect.x, rect.y)
        }
    }

    override fun loadTexture(manager: IResourceManager) {
        packer = RectanglePacker<BitGrid>(width, height, 1)
        rects.clear()
        this.bindTexture()
        TextureUtil.prepareImage(glTextureId, width, height)
        val native = NativeImage(width, height, true)
        native.uploadTextureSub(0, 0, 0, false)
        native.close()

        val solidGrid = BitGrid(3, 3)
        for(y in 0 until 3)
            for(x in 0 until 3)
                solidGrid[x, y] = true
        val realSolidRect = rectFor(solidGrid)
        // Of the 3x3 solid rectangle, we only actually use the center pixel
        solidRect = rect(realSolidRect.x + 1, realSolidRect.y + 1, 1, 1)
    }
}