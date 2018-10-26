package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.PngSizeInfo



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
    /**
     * The minimum U coordinate in pixels
     */
    var u: Int = 0
        protected set
    /**
     * The minimum V coordinate in pixels
     */
    var v: Int = 0
        protected set
    /**
     * The width in pixels
     */
    var uvWidth: Int = 0
        protected set
    /**
     * The height in pixels
     */
    var uvHeight: Int = 0
        protected set
    protected var frames: IntArray = IntArray(0)
    protected var offsetU: Int = 0
    protected var offsetV: Int = 0
    override val frameCount: Int
        get() = frames.size

    /**
     * The width on screen of the sprite.

     * Public for easy and concise access. Set to 1 by default.
     */
    override var width = 1

    /**
     * The height on screen of the sprite.

     * Public for easy and concise access. Set to 1 by default.
     */
    override var height = 1

    constructor(tex: Texture, u: Int, v: Int, width: Int, height: Int, frames: IntArray, offsetU: Int, offsetV: Int) {
        this.tex = tex
        init(u, v, width, height, frames, offsetU, offsetV)
    }

    @Suppress("LeakingThis")
    @JvmOverloads constructor(
            loc: ResourceLocation,
            width: Int = 0,
            height: Int = 0) {
        this.tex = Texture(loc)

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

        this.u = 0
        this.v = 0
        this.uvWidth = pngWidth
        this.uvHeight = pngHeight
        this.width = pngWidth
        this.height = pngHeight
        this.frames = IntArray(0)
    }

    /**
     * Initializes the sprite. Used to reinitialize on resource pack reload.

     * --Package private--
     */
    internal fun init(u: Int, v: Int, width: Int, height: Int, frames: IntArray, offsetU: Int, offsetV: Int) {
        this.u = u
        this.v = v
        this.uvWidth = width
        this.uvHeight = height
        this.offsetU = offsetU
        this.offsetV = offsetV
        this.frames = frames
    }

    /**
     * The minimum U coordinate (0-1)
     */
    override fun minU(animFrames: Int): Float {
        return (u + offsetU * if (frames.isEmpty()) 0 else frames[animFrames % frames.size]).toFloat() / tex.width.toFloat()
    }

    /**
     * The minimum V coordinate (0-1)
     */
    override fun minV(animFrames: Int): Float {
        return (v + offsetV * if (frames.isEmpty()) 0 else frames[animFrames % frames.size]).toFloat() / tex.height.toFloat()
    }

    /**
     * The maximum U coordinate (0-1)
     */
    override fun maxU(animFrames: Int): Float {
        return (u + uvWidth + offsetU * if (frames.isEmpty()) 0 else frames[animFrames % frames.size]).toFloat() / tex.width.toFloat()
    }

    /**
     * The maximum V coordinate (0-1)
     */
    override fun maxV(animFrames: Int): Float {
        return (v + uvHeight + offsetV * if (frames.isEmpty()) 0 else frames[animFrames % frames.size]).toFloat() / tex.height.toFloat()
    }

    fun getSubSprite(u: Int, v: Int, width: Int, height: Int): Sprite {
        val uScale = uvWidth.toFloat() / this.width.toFloat()
        val vScale = uvHeight.toFloat() / this.height.toFloat()
        val s = Sprite(this.tex, this.u + (u * uScale).toInt(), this.v + (v * vScale).toInt(), (width * uScale).toInt(), (height * vScale).toInt(), frames, offsetU, offsetV)
        s.width = width
        s.height = height
        return s
    }

    override fun bind() = tex.bind()
}
