package com.teamwizardry.librarianlib.glitter.testmod.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.mixinCast
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import net.minecraft.util.math.vector.Matrix4f
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

    @Suppress("LocalVariableName")
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        RenderSystem.disableTexture()
        if(blend) {
            RenderSystem.enableBlend()
        } else {
            RenderSystem.disableBlend()
        }
        if(blendFactors != null) {
            RenderSystem.blendFunc(blendFactors.first.param, blendFactors.second.param)
        }
        RenderSystem.depthMask(depthMask)
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.003921569F)
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableCull()
        RenderSystem.lineWidth(size)

        val modelViewMatrix = matrixStack.last.matrix
        val transformMatrix = mixinCast<IMatrix4f>(modelViewMatrix)
        val tm00 = transformMatrix.m00
        val tm01 = transformMatrix.m01
        val tm02 = transformMatrix.m02
        val tm03 = transformMatrix.m03
        val tm10 = transformMatrix.m10
        val tm11 = transformMatrix.m11
        val tm12 = transformMatrix.m12
        val tm13 = transformMatrix.m13
        val tm20 = transformMatrix.m20
        val tm21 = transformMatrix.m21
        val tm22 = transformMatrix.m22
        val tm23 = transformMatrix.m23

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

        particles.forEach { particle ->
            for(i in prepModules.indices) {
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

            var startX = x
            var startY = y
            var startZ = z
            var endX = x + velocity.contents[0]
            var endY = y + velocity.contents[1]
            var endZ = z + velocity.contents[2]

            val _startX = startX
            val _startY = startY
            val _startZ = startZ
            startX = tm00 * _startX + tm01 * _startY + tm02 * _startZ + tm03 * 1
            startY = tm10 * _startX + tm11 * _startY + tm12 * _startZ + tm13 * 1
            startZ = tm20 * _startX + tm21 * _startY + tm22 * _startZ + tm23 * 1

            val _endX = endX
            val _endY = endY
            val _endZ = endZ
            endX = tm00 * _endX + tm01 * _endY + tm02 * _endZ + tm03 * 1
            endY = tm10 * _endX + tm11 * _endY + tm12 * _endZ + tm13 * 1
            endZ = tm20 * _endX + tm21 * _endY + tm22 * _endZ + tm23 * 1

            vb.pos(startX, startY, startZ).color(r, g, b, a).endVertex()
            vb.pos(endX, endY, endZ).color(r, g, b, a).endVertex()
        }

        tessellator.draw()

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param)
        RenderSystem.enableCull()
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F)
        RenderSystem.depthMask(true)
        RenderSystem.disableBlend()
        RenderSystem.enableTexture()
    }
}
