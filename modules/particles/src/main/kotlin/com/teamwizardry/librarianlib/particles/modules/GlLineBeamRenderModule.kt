package com.teamwizardry.librarianlib.particles.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.particles.ParticleRenderModule
import com.teamwizardry.librarianlib.particles.ParticleUpdateModule
import com.teamwizardry.librarianlib.particles.ReadParticleBinding
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * A simple beam rendering module using GL_LINES.
 *
 * This is mainly designed as an example of how different renderers can interpret the particle array differently. In
 * this case the renderer starts drawing with GL_LINES and connecting each particle to the last until it reaches one
 * with a non-zero value in [isEnd], at which point it breaks the chain and uses the next particle as the starts of the
 * next chain of lines.
 */
class GlLineBeamRenderModule(
        /**
         * When this binding is non-zero the chain of lines will be broken, this particle being the end of the current
         * chain and the next being the start of the next chain.
         */
        @JvmField val isEnd: ReadParticleBinding,
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
        isEnd.require(1)
        previousPosition.require(3)
        position.require(3)
        color.require(4)
        alpha?.require(1)
    }
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        GlStateManager.disableTexture()
        if(blend) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }
        if(blendFactors != null) {
            GlStateManager.blendFunc(blendFactors.first.param, blendFactors.second.param)
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

        var isStart = true
        var prevX = Double.NaN
        var prevY = Double.NaN
        var prevZ = Double.NaN
        var prevR = Float.NaN
        var prevG = Float.NaN
        var prevB = Float.NaN
        var prevA = Float.NaN

        particles.forEach { particle ->
            for(i in 0 until prepModules.size) {
                prepModules[i].update(particle)
            }

            previousPosition.load(particle)
            position.load(particle)
            color.load(particle)
            alpha?.load(particle)
            isEnd.load(particle)

            val x = Client.worldTime.interp(previousPosition.contents[0], position.contents[0])
            val y = Client.worldTime.interp(previousPosition.contents[1], position.contents[1])
            val z = Client.worldTime.interp(previousPosition.contents[2], position.contents[2])

            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            var a = color.contents[3].toFloat()
            if(alpha != null)
                a *= alpha.contents[0].toFloat()

            if(isStart) {
                isStart = false
            } else {
                vb.pos(prevX, prevY, prevZ).color(prevR, prevG, prevB, prevA).endVertex()
                vb.pos(x, y, z).color(r, g, b, a).endVertex()
            }

            if(isEnd.contents[0] != 0.0) {
                isStart = true
            } else {
                prevX = x
                prevY = y
                prevZ = z
                prevR = r
                prevG = g
                prevB = b
                prevA = a
            }
        }

        tessellator.draw()

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param)
        GlStateManager.enableCull()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture()
    }
}
