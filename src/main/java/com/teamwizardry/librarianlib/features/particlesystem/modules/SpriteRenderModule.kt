package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.require
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11

class SpriteRenderModule(
        private val sprite: ResourceLocation,
        private val blend: Boolean,
        private val previousPosition: ReadParticleBinding,
        private val position: ReadParticleBinding,
        private val color: ReadParticleBinding,
        private val size: ReadParticleBinding,
        private val facingVector: ReadParticleBinding? = null,
        private val alpha: ReadParticleBinding?,
        private val blendFactors: Pair<GlStateManager.SourceFactor, GlStateManager.DestFactor>? = null,
        private val depthMask: Boolean = true
): ParticleRenderModule {
    init {
        previousPosition.require(3)
        position.require(3)
        color.require(4)
        size.require(1)
        facingVector?.require(3)
        alpha?.require(1)
    }
    override fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        Minecraft.getMinecraft().renderEngine.bindTexture(sprite)

        val player = Minecraft.getMinecraft().player
        GlStateManager.enableTexture2D()
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


        val playerYaw = player.prevRotationYaw.toDouble()
        val playerPitch = player.prevRotationPitch.toDouble()

        val yawX = Math.sin(-Math.toRadians(playerYaw+180))
        val yawZ = Math.cos(-Math.toRadians(playerYaw+180))
        val yawScale = Math.sin(-Math.toRadians(playerPitch))
        val pitchY = Math.cos(-Math.toRadians(playerPitch))

        var iHatX = yawZ
        var iHatY = 0.0
        var iHatZ = -yawX

        var jHatX = yawX*yawScale
        var jHatY = pitchY
        var jHatZ = yawZ*yawScale

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)

        particles.forEach { particle ->
            for(i in 0 until prepModules.size) {
                prepModules[i].update(particle)
            }
            if(facingVector != null) {
                val facingX = facingVector[particle, 0]
                val facingY = facingVector[particle, 1]
                val facingZ = facingVector[particle, 2]
                // x axis, facing • (0, 1, 0)
                iHatX = -facingZ
                iHatY = 0.0
                iHatZ = facingX
                val iHatInvLength = MathHelper.fastInvSqrt(iHatX * iHatX + iHatY * iHatY + iHatZ * iHatZ)
                iHatX *= iHatInvLength
                iHatY *= iHatInvLength
                iHatZ *= iHatInvLength

                // y axis, facing • iHat
                jHatX = facingY * facingX
                jHatY = facingZ * -facingZ - facingX * facingX
                jHatZ = facingY * facingZ
                val jHatInvLength = MathHelper.fastInvSqrt(jHatX * jHatX + jHatY * jHatY + jHatZ * jHatZ)
                jHatX *= -jHatInvLength
                jHatY *= -jHatInvLength
                jHatZ *= -jHatInvLength
            }

            val size = this.size[particle, 0] /2
            val localIHatX = iHatX * size
            val localIHatY = iHatY * size
            val localIHatZ = iHatZ * size
            val localJHatX = jHatX * size
            val localJHatY = jHatY * size
            val localJHatZ = jHatZ * size

            val x = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 0], position[particle, 0])
            val y = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 1], position[particle, 1])
            val z = ClientTickHandler.interpWorldPartialTicks(previousPosition[particle, 2], position[particle, 2])

            val r = color[particle, 0].toFloat()
            val g = color[particle, 1].toFloat()
            val b = color[particle, 2].toFloat()
            var a = color[particle, 3].toFloat()
            if(alpha != null)
                a *= alpha[particle, 0].toFloat()

            vb.pos(x-localIHatX-localJHatX, y-localIHatY-localJHatY, z-localIHatZ-localJHatZ).tex(0.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x+localIHatX-localJHatX, y+localIHatY-localJHatY, z+localIHatZ-localJHatZ).tex(1.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x+localIHatX+localJHatX, y+localIHatY+localJHatY, z+localIHatZ+localJHatZ).tex(1.0, 1.0).color(r, g, b, a).endVertex()
            vb.pos(x-localIHatX+localJHatX, y-localIHatY+localJHatY, z-localIHatZ+localJHatZ).tex(0.0, 1.0).color(r, g, b, a).endVertex()
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