package com.teamwizardry.librarianlib.glitter.test.modules

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
/**
 * A simple velocity vector rendering module using GL_LINES.
 */
class VelocityRenderModule(
    /**
     * Whether to enable blending in OpenGL
     */
    @JvmField val blend: Boolean,
    /**
     * The previous position binding. This is used to interpolate between ticks
     */
    @JvmField val previousPosition: ReadParticleBinding,
    /**
     * The current position binding.
     */
    @JvmField val position: ReadParticleBinding,
    /**
     * The current velocity binding.
     */
    @JvmField val velocity: ReadParticleBinding,
    /**
     * The color of the line
     */
    @JvmField val color: ReadParticleBinding,
    /**
     * The width of the line in pixels
     */
    @JvmField val size: Float,
    /**
     * The alpha multiplier for the color. If null this defaults to `1.0`
     */
    @JvmField val alpha: ReadParticleBinding?,
    /**
     * The pair of source/dest enableBlend factors to use while rendering, or the default if null.
     */
    @JvmField val blendFactors: Pair<GlStateManager.SrcFactor, GlStateManager.DstFactor>? = null,
    /**
     * Whether to enable the depth mask (false = don't write to the depth buffer)
     */
    @JvmField val depthMask: Boolean = true,

    @JvmField val scale: Double = 1.0
): ParticleRenderModule {
    init {
        previousPosition.require(3)
        position.require(3)
        velocity.require(3)
        color.require(4)
        alpha?.require(1)
    }

    @Suppress("LocalVariableName")
    override fun renderDirect(
        context: WorldRenderContext,
        particles: List<DoubleArray>,
        prepModules: List<ParticleUpdateModule>
    ) {
    }
}
