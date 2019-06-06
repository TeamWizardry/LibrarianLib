package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.PngSizeInfo
import java.awt.image.BufferedImage
import java.awt.image.RasterFormatException
import kotlin.math.max

/**
 * This class represents a section of a [Texture]
 */
@SideOnly(Side.CLIENT)
open class Sprite : ISprite {

    /**
     * The [Texture] that this sprite is a part of
     * @return
     */
    var tex: Texture
        protected set
    protected var def: SpriteDefinition = SpriteDefinition("")

    val name: String
        get() = def.name

    override val pinTop: Boolean
        get() = def.pinTop
    override val pinBottom: Boolean
        get() = def.pinBottom
    override val pinLeft: Boolean
        get() = def.pinLeft
    override val pinRight: Boolean
        get() = def.pinRight

    override val frameCount: Int
        get() = max(1, def.frames.size)

    override val width: Int
        get() = def.w / tex.logicalScale

    override val height: Int
        get() = def.h / tex.logicalScale

    override val uSize: Float
        get() = def.w / tex.width.toFloat()
    override val vSize: Float
        get() = def.h / tex.height.toFloat()

    var images: List<BufferedImage> = listOf()
        private set

    constructor(tex: Texture) {
        this.tex = tex
    }

    @Suppress("LeakingThis")
    @JvmOverloads constructor(
            loc: ResourceLocation, width: Int = 0, height: Int = 0) {
        val pngSizeInfo = PngSizeInfo.makeFromResource(Minecraft().resourceManager.getResource(loc))
        var pngWidth = pngSizeInfo.pngWidth
        var pngHeight = pngSizeInfo.pngHeight

        if (width > 0 && height <= 0) {
            pngWidth = width
            pngHeight = pngHeight * width / pngWidth
        } else if (width <= 0 && height > 0) {
            pngHeight = height
            pngWidth = pngWidth * height / pngHeight
        } else if (width > 0 && height > 0) {
            pngWidth = width
            pngHeight = height
        }

        def.u = 0
        def.v = 0
        def.w = pngWidth
        def.h = pngHeight
        def.frames = IntArray(0)

        this.tex = Texture(loc, pngWidth, pngHeight)
        tex.sprites[loc.path] = this

        tex.load()
    }

    /**
     * Initializes the sprite. Used to reinitialize on resource pack reload.

     * --Package private--
     */
    internal fun init(def: SpriteDefinition) {
        this.def = def
    }

    fun loadImage(full: BufferedImage) {
        var exception: Exception? = null
        images = (0 until frameCount).map { i ->
            val minX = (minU(i) * full.width).toInt()
            val maxX = (maxU(i) * full.width).toInt()
            val minY = (minV(i) * full.height).toInt()
            val maxY = (maxV(i) * full.height).toInt()

            try {
                full.getSubimage(minX, minY, maxX - minX, maxY - minY)
            } catch(e: RasterFormatException) {
                exception = e
                BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            }
        }
        exception?.also { throw it }
    }

    private fun frameMultiplier(animFrames: Int) = if (def.frames.isEmpty()) 0 else def.frames[animFrames % def.frames.size]

    /**
     * The minimum U coordinate (0-1)
     */
    override fun minU(animFrames: Int): Float {
        return (def.u + def.offsetU * frameMultiplier(animFrames)).toFloat() / tex.width.toFloat()
    }

    /**
     * The minimum V coordinate (0-1)
     */
    override fun minV(animFrames: Int): Float {
        return (def.v + def.offsetV * frameMultiplier(animFrames)).toFloat() / tex.height.toFloat()
    }

    /**
     * The maximum U coordinate (0-1)
     */
    override fun maxU(animFrames: Int): Float {
        return (def.u + def.w + def.offsetU * frameMultiplier(animFrames)).toFloat() / tex.width.toFloat()
    }

    /**
     * The maximum V coordinate (0-1)
     */
    override fun maxV(animFrames: Int): Float {
        return (def.v + def.h + def.offsetV * frameMultiplier(animFrames)).toFloat() / tex.height.toFloat()
    }

    override val minUCap: Float
        get() = def.minUCap.toFloat() / def.w

    override val minVCap: Float
        get() = def.minVCap.toFloat() / def.h

    override val maxUCap: Float
        get() = def.maxUCap.toFloat() / def.w

    override val maxVCap: Float
        get() = def.maxVCap.toFloat() / def.h

    override fun bind() = tex.bind()


    override fun toString(): String {
        return "Sprite(texture=${tex.loc}, name=$name)"
    }
}
