package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import net.minecraft.client.render.RenderLayer

/**
 * [Sprite] wrapper for Minecraft [atlas sprites](net.minecraft.client.texture.Sprite).
 *
 * This sprite can't control the current animation frame, that's handled globally by the atlas.
 */
public class MinecraftAtlasSprite(private val sprite: net.minecraft.client.texture.Sprite) : Sprite {

    override val renderType: RenderLayer = SimpleRenderLayers.flat(sprite.atlas.id)

    override fun minU(animFrames: Int): Float = sprite.minU

    override fun minV(animFrames: Int): Float = sprite.minV

    override fun maxU(animFrames: Int): Float = sprite.maxU

    override fun maxV(animFrames: Int): Float = sprite.maxV

    override val width: Int
        get() = sprite.width

    override val height: Int
        get() = sprite.height

    override val frameCount: Int
        get() = 1

    override val uSize: Float
        get() = sprite.maxU - sprite.minU
    override val vSize: Float
        get() = sprite.maxV - sprite.minV
}
