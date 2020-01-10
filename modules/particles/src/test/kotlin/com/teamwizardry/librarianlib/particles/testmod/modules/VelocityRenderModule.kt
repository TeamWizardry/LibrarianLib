package com.teamwizardry.librarianlib.particles.testmod.modules

import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.particles.ParticleRenderModule
import com.teamwizardry.librarianlib.particles.ParticleUpdateModule
import com.teamwizardry.librarianlib.particles.ReadParticleBinding
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

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
    @JvmField val blendFactors: Pair<GlStateManager.SourceFactor, GlStateManager.DestFactor>? = null,
    /**
     * Whether to enable the depth mask (false = don't write to the depth buffer)
     */
    @JvmField val depthMask: Boolean = true
): ParticleRenderModule {
    init {
        previousPosition.require(3)
        position.require(3)
        velocity.require(3)
        color.require(4)
        alpha?.require(1)
    }

    override fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        GlStateManager.disableTexture()
        if(blend) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }
        if(blendFactors != null) {
            GlStateManager.blendFunc(blendFactors.first, blendFactors.second)
        }
        GlStateManager.depthMask(depthMask)
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F)
        GlStateManager.disableLighting()
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableCull()
        GlStateManager.lineWidth(size)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

        particles.forEach { particle ->
            for(i in 0 until prepModules.size) {
                prepModules[i].update(particle)
            }

            previousPosition.load(particle)
            position.load(particle)
            velocity.load(particle)
            color.load(particle)
            alpha?.load(particle)

            val x = Client.worldTime.interp(previousPosition.contents[0], position.contents[0])
            val y = Client.worldTime.interp(previousPosition.contents[1], position.contents[1])
            val z = Client.worldTime.interp(previousPosition.contents[2], position.contents[2])

            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            var a = color.contents[3].toFloat()
            if(alpha != null)
                a *= alpha.contents[0].toFloat()

            vb.pos(x, y, z).color(r, g, b, a).endVertex()
            vb.pos(x+velocity.contents[0], y+velocity.contents[1], z+velocity.contents[2]).color(r, g, b, a).endVertex()
        }

        tessellator.draw()

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableCull()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture()
    }
}
