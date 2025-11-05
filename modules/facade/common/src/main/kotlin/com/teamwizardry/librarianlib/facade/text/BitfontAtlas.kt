package com.teamwizardry.librarianlib.facade.text

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.TextureUtil
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.rect
import com.teamwizardry.librarianlib.math.Rect2d
import dev.thecodewarrior.bitfont.data.BitGrid
import dev.thecodewarrior.bitfont.utils.RectanglePacker
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.NativeImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11

public object BitfontAtlas: AbstractTexture() {
    public val ATLAS_LOCATION: Identifier = Identifier("librarianlib:textures/atlas/bitfont.png")

    public var width: Int = 128
        private set
    public var height: Int = 128
        private set

    private val gpuMaxTexSize = GlStateManager._getInteger(GL11.GL_MAX_TEXTURE_SIZE)

    private var packer = RectanglePacker<BitGrid>(width, height, 1)
    private val rects = mutableMapOf<BitGrid, RectanglePacker.Rectangle>()
    private var solidRect: Rect2d = Rect2d.ZERO

    init {
        Client.textureManager.registerTexture(ATLAS_LOCATION, this)
    }

    public fun solidTex(): Rect2d {
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(solidRect.x / width, solidRect.y / height, solidRect.width / width, solidRect.height / height)
    }

    public fun rectFor(image: BitGrid): Rect2d {
        var rect = rects[image]
        if (rect == null) {
            insert(image)
            rect = rects[image] ?: throw IllegalStateException()
        }
        val width = width.toDouble()
        val height = height.toDouble()
        return rect(rect.x / width, rect.y / height, rect.width / width, rect.height / height)
    }

    public fun load(images: List<BitGrid>) {
        images.forEach { insert(it) }
    }

    public fun insert(image: BitGrid) {
        if (image in rects)
            return
        var newRect: RectanglePacker.Rectangle? = packer.insert(image.width, image.height, image)
        if (newRect == null) {
            expand()
            newRect = packer.insert(image.width, image.height, image) ?: return
        }
        rects[image] = newRect
        draw(image, newRect.x, newRect.y)
    }

    private fun draw(image: BitGrid, xOrigin: Int, yOrigin: Int) {
        val native = NativeImage(image.width, image.height, true)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (image[x, y]) {
                    native.setColor(x, y, 0.inv())
                }
            }
        }

        this.bindTexture()
        native.upload(0, xOrigin, yOrigin, false)
        native.close()
    }

    private fun expand() {
        width *= 2
        height *= 2
        if (width > gpuMaxTexSize || height > gpuMaxTexSize)
            throw IllegalStateException("Ran out of atlas space! OpenGL max texture size: " +
                "$gpuMaxTexSize x $gpuMaxTexSize and managed to fit ${rects.size} glyphs.")
        packer.expand(width, height)
        TextureUtil.prepareImage(glId, width, height)
        rects.forEach { (image, rect) ->
            draw(image, rect.x, rect.y)
        }
    }

    override fun load(manager: ResourceManager) {
        packer = RectanglePacker<BitGrid>(width, height, 1)
        rects.clear()
        this.bindTexture()
        TextureUtil.prepareImage(glId, width, height)
        val native = NativeImage(width, height, true)
        native.upload(0, 0, 0, false)
        native.close()

        val solidGrid = BitGrid(3, 3)
        for (y in 0 until 3)
            for (x in 0 until 3)
                solidGrid[x, y] = true
        val realSolidRect = rectFor(solidGrid)
        // Of the 3x3 solid rectangle, we only actually use the center pixel
        solidRect = rect(realSolidRect.x + 1, realSolidRect.y + 1, 1, 1)
    }
}