package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.require
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11

class GlLineBeamRenderModule(
        private val isEnd: ReadParticleBinding,
        private val blend: Boolean,
        private val previousPosition: ReadParticleBinding,
        private val position: ReadParticleBinding,
        private val color: ReadParticleBinding,
        private val size: Float,
        private val alpha: ReadParticleBinding?,
        private val blendFactors: Pair<GlStateManager.SourceFactor, GlStateManager.DestFactor>? = null,
        private val depthMask: Boolean = true
): ParticleRenderModule {
    init {
        isEnd.require(1)
        previousPosition.require(3)
        position.require(3)
        color.require(4)
        alpha?.require(1)
    }
    override fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        GlStateManager.disableTexture2D()
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
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableCull()
        GlStateManager.glLineWidth(size)

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

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

            val x = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 0], position[particle, 0])
            val y = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 1], position[particle, 1])
            val z = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 2], position[particle, 2])

            val r = color[particle, 0].toFloat()
            val g = color[particle, 1].toFloat()
            val b = color[particle, 2].toFloat()
            var a = color[particle, 3].toFloat()
            if(alpha != null)
                a *= alpha[particle, 0].toFloat()

            if(!(prevX.isNaN() || prevY.isNaN() || prevZ.isNaN() ||
                    prevR.isNaN() || prevG.isNaN() || prevB.isNaN() || prevA.isNaN())) {
                vb.pos(prevX, prevY, prevZ).color(prevR, prevG, prevB, prevA).endVertex()
                vb.pos(x, y, z).color(r, g, b, a).endVertex()
            }

            if(isEnd[particle, 0] != 0.0) {
                prevX = Double.NaN
                prevY = Double.NaN
                prevZ = Double.NaN
                prevR = Float.NaN
                prevG = Float.NaN
                prevB = Float.NaN
                prevA = Float.NaN
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

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableCull()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
    }
}
