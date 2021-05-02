package com.teamwizardry.librarianlib.mosaic

import com.teamwizardry.librarianlib.core.rendering.DefaultRenderPhases
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

public abstract class WrappedSprite: Sprite {
    public abstract val wrapped: Sprite?

    override fun minU(animFrames: Int): Float = wrapped?.minU(animFrames) ?: 0f
    override fun minV(animFrames: Int): Float = wrapped?.minV(animFrames) ?: 0f
    override fun maxU(animFrames: Int): Float = wrapped?.maxU(animFrames) ?: 1f
    override fun maxV(animFrames: Int): Float = wrapped?.maxV(animFrames) ?: 1f

    override val renderType: RenderLayer get() = wrapped?.renderType ?: missingType
    override val width: Int get() = wrapped?.width ?: 1
    override val height: Int get() = wrapped?.height ?: 1
    override val uSize: Float get() = wrapped?.uSize ?: 1f
    override val vSize: Float get() = wrapped?.vSize ?: 1f
    override val frameCount: Int get() = wrapped?.frameCount ?: 1
    override val minUCap: Float get() = wrapped?.minUCap ?: 0f
    override val minVCap: Float get() = wrapped?.minVCap ?: 0f
    override val maxUCap: Float get() = wrapped?.maxUCap ?: 0f
    override val maxVCap: Float get() = wrapped?.maxVCap ?: 0f
    override val pinTop: Boolean get() = wrapped?.pinTop ?: true
    override val pinBottom: Boolean get() = wrapped?.pinBottom ?: true
    override val pinLeft: Boolean get() = wrapped?.pinLeft ?: true
    override val pinRight: Boolean get() = wrapped?.pinRight ?: true
    override val rotation: Int get() = wrapped?.rotation ?: 0

    override fun draw(matrix: Matrix4d, x: Float, y: Float, animTicks: Int, tint: Color) {
        wrapped?.draw(matrix, x, y, animTicks, tint)
    }
    override fun draw(matrix: Matrix4d, x: Float, y: Float, width: Float, height: Float, animTicks: Int, tint: Color) {
        wrapped?.draw(matrix, x, y, width, height, animTicks, tint)
    }

    private companion object {
        @Suppress("INACCESSIBLE_TYPE")
        val missingType: RenderLayer = run {
            val renderState = RenderLayer.MultiPhaseParameters.builder()
                .texture(RenderPhase.Texture(Identifier("minecraft:missingno"), false, false))
                .alpha(DefaultRenderPhases.ONE_TENTH_ALPHA)
                .depthTest(DefaultRenderPhases.LEQUAL_DEPTH_TEST)
                .transparency(DefaultRenderPhases.TRANSLUCENT_TRANSPARENCY)

            RenderLayer.of("sprite_type",
                VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, false, false, renderState.build(true)
            )
        }
    }
}