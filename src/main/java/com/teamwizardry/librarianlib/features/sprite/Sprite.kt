package com.teamwizardry.librarianlib.features.sprite

import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.PngSizeInfo
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

    override val hardScaleU: Boolean
        get() = def.hardScaleU

    override val hardScaleV: Boolean
        get() = def.hardScaleV

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

    constructor(tex: Texture, def: SpriteDefinition) {
        this.tex = tex
        init(def)
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

        this.tex = Texture(loc, pngWidth, pngHeight)

        def.u = 0
        def.v = 0
        def.w = pngWidth
        def.h = pngHeight
        def.frames = IntArray(0)
    }

    /**
     * Initializes the sprite. Used to reinitialize on resource pack reload.

     * --Package private--
     */
    internal fun init(def: SpriteDefinition) {
        this.def = def
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
}

class SpriteDefinition(
    var name: String,
    var u: Int, var v: Int, var w: Int, var h: Int,
    var frames: IntArray, var offsetU: Int, var offsetV: Int,
    var minUCap: Int, var minVCap: Int, var maxUCap: Int, var maxVCap: Int,
    var hardScaleU: Boolean, var hardScaleV: Boolean) {
    constructor(name: String) : this(
        name,
        0, 0, 0, 0,
        intArrayOf(0), 0, 0,
        0, 0, 0, 0,
        false, false
    )
}
