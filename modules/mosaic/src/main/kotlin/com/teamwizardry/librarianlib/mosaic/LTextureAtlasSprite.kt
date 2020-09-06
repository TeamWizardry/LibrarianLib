package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite

/**
 * [ISprite] wrapper for [TextureAtlasSprite].
 * Should ONLY be used when a [Sprite] can't be used.
 *
 * Nothing special needs to be done for animations to work ([TextureAtlasSprite] handles them out of the box).
 */
public class LTextureAtlasSprite(private val tas: TextureAtlasSprite) : ISprite {

    override val renderType: RenderType = SimpleRenderTypes.flat(tas.atlasTexture.textureLocation)

    override fun minU(animFrames: Int): Float = tas.minU

    override fun minV(animFrames: Int): Float = tas.minV

    override fun maxU(animFrames: Int): Float = tas.maxU

    override fun maxV(animFrames: Int): Float = tas.maxV

    override val width: Int
        get() = tas.width

    override val height: Int
        get() = tas.height

    override val frameCount: Int
        get() = 1

    override val uSize: Float
        get() = tas.maxU - tas.minU
    override val vSize: Float
        get() = tas.maxV - tas.minV
}
