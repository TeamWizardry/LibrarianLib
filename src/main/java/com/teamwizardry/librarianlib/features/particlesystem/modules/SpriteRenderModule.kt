package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import com.teamwizardry.librarianlib.features.particlesystem.require
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11

/**
 * The bread-and-butter render module, a simple billboarded sprite.
 *
 * Particles are drawn as dynamically sized/colored sprites that are either billboarded or with an arbitrary facing
 * defined by [facingVector]. The particles are drawn as squares [size] meters to a side and centered on the the
 * particle's position. One thing of note is that for some particle effects, particularly ones that should look
 * consistent, disabling interpolation by passing the current position for both [previousPosition] and [position] can
 * make the particles rock solid in their positions as opposed to jittering about.
 */
class SpriteRenderModule @JvmOverloads constructor(
        /**
         * The sprite texture to use
         */
        @JvmField val sprite: ResourceLocation,
        /**
         * Whether to enable OpenGL blending
         */
        @JvmField val blend: Boolean = false,
        /**
         * The current position of the particle
         */
        @JvmField val position: ReadParticleBinding,
        /**
         * The position of the particle last tick, used to interpolate between ticks
         */
        @JvmField val previousPosition: ReadParticleBinding? = null,
        /**
         * The OpenGL color of the particle
         */
        @JvmField val color: ReadParticleBinding = ConstantBinding(255.0, 255.0, 255.0, 255.0),
        /**
         * The width and height of the particle in meters
         */
        @JvmField val size: ReadParticleBinding = ConstantBinding(1.0),
        /**
         * If present, an artificial facing vector used instead of the player's look vector. This vector _does not need
         * to be normalized_ as normalization is already being done for an unrelated reason. The additional computation
         * is unnecessary and will lead to more performance degradation than is required for this feature.
         */
        @JvmField val facingVector: ReadParticleBinding? = null,
        /**
         * The alpha multiplier for the color. Defaults to 1 if not present.
         */
        @JvmField val alpha: ReadParticleBinding? = ConstantBinding(1.0),
        /**
         * The OpenGL source/dest blend factors. Leave null to keep the defaults.
         */
        @JvmField val blendFactors: Pair<GlStateManager.SourceFactor, GlStateManager.DestFactor>? = null,
        /**
         * Whether to enable OpenGL depth masking. (false = no writing to the depth buffer)
         */
        @JvmField val depthMask: Boolean = true
) : ParticleRenderModule {
    init {
        previousPosition?.require(3)
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
        if (blend) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }
        if (blendFactors != null) {
            GlStateManager.blendFunc(blendFactors.first, blendFactors.second)
        }
        GlStateManager.depthMask(depthMask)
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F)
        GlStateManager.disableLighting()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableCull()


        val playerYaw = player.prevRotationYaw.toDouble()
        val playerPitch = player.prevRotationPitch.toDouble()

        val yawX = MathHelper.sin(-Math.toRadians(playerYaw + 180).toFloat()).toDouble()
        val yawZ = MathHelper.cos(-Math.toRadians(playerYaw + 180).toFloat()).toDouble()
        val yawScale = MathHelper.sin(-Math.toRadians(playerPitch).toFloat()).toDouble()
        val pitchY = MathHelper.cos(-Math.toRadians(playerPitch).toFloat()).toDouble()

        var iHatX = yawZ
        var iHatY = 0.0
        var iHatZ = -yawX

        var jHatX = yawX * yawScale
        var jHatY = pitchY
        var jHatZ = yawZ * yawScale

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)

        particles.forEach { particle ->
            for (i in 0 until prepModules.size) {
                prepModules[i].update(particle)
            }
            if (facingVector != null) {
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

            val size = this.size[particle, 0] / 2
            val localIHatX = iHatX * size
            val localIHatY = iHatY * size
            val localIHatZ = iHatZ * size
            val localJHatX = jHatX * size
            val localJHatY = jHatY * size
            val localJHatZ = jHatZ * size

            var x = position[particle, 0]
            previousPosition?.let { x = ClientTickHandler.interpWorldPartialTicks(it[particle, 0], x) }
            var y = position[particle, 1]
            previousPosition?.let { y = ClientTickHandler.interpWorldPartialTicks(it[particle, 1], y) }
            var z = position[particle, 2]
            previousPosition?.let { z = ClientTickHandler.interpWorldPartialTicks(it[particle, 2], z) }

            val r = color[particle, 0].toFloat()
            val g = color[particle, 1].toFloat()
            val b = color[particle, 2].toFloat()
            var a = color[particle, 3].toFloat()
            if (alpha != null)
                a *= alpha[particle, 0].toFloat()

            vb.pos(x - localIHatX - localJHatX, y - localIHatY - localJHatY, z - localIHatZ - localJHatZ).tex(0.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x + localIHatX - localJHatX, y + localIHatY - localJHatY, z + localIHatZ - localJHatZ).tex(1.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x + localIHatX + localJHatX, y + localIHatY + localJHatY, z + localIHatZ + localJHatZ).tex(1.0, 1.0).color(r, g, b, a).endVertex()
            vb.pos(x - localIHatX + localJHatX, y - localIHatY + localJHatY, z - localIHatZ + localJHatZ).tex(0.0, 1.0).color(r, g, b, a).endVertex()
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