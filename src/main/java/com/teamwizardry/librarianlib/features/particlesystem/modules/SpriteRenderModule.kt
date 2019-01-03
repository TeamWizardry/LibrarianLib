package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.BlendMode
import com.teamwizardry.librarianlib.features.particlesystem.ParticleRenderModule
import com.teamwizardry.librarianlib.features.particlesystem.ParticleUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.ReadParticleBinding
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
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
         * The current position of the particle
         */
        @JvmField val position: ReadParticleBinding,
        /**
         * The position of the particle last tick, used to interpolate between ticks
         */
        @JvmField val previousPosition: ReadParticleBinding? = null,
        /**
         * The OpenGL colorPrimary of the particle
         */
        @JvmField val color: ReadParticleBinding = ConstantBinding(1.0, 1.0, 1.0, 1.0),
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
         * The alpha multiplier for the colorPrimary. Defaults to 1 if not present.
         */
        @JvmField val alphaMultiplier: ReadParticleBinding = ConstantBinding(1.0),
        /**
         * The OpenGL source/dest enableBlend factors. Leave null to keep the defaults.
         */
        @JvmField val blendMode: BlendMode = BlendMode.NORMAL,
        /**
         * Whether to enable OpenGL depth masking. (false = no writing to the depth buffer)
         */
        @JvmField val depthMask: Boolean = false,
        /**
         * Whether to enable OpenGL blending
         */
        @JvmField val enableBlend: Boolean = true,
        /**
         * If disabled, will render the particles in 3d space (the particle face vectors will be against that of the
         * player in 3D).
         * If enabled, will render the particles with flat vectors so they always face the screen (like in a gui).
         */
        @JvmField val is2D: Boolean = false
) : ParticleRenderModule {
    init {
        previousPosition?.require(3)
        position.require(3)
        color.require(4)
        size.require(1)
        facingVector?.require(3)
        alphaMultiplier.require(1)
    }

    override fun render(particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        Minecraft.getMinecraft().renderEngine.bindTexture(sprite)

        val player = Minecraft.getMinecraft().player
        GlStateManager.enableTexture2D()
        if (enableBlend) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }

        blendMode.glApply()

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
                facingVector.load(particle)
                val facingX = facingVector.getValue(0)
                val facingY = facingVector.getValue(1)
                val facingZ = facingVector.getValue(2)
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

            size.load(particle)
            val size = this.size.getValue(0) / 2
            val localIHatX = iHatX * size
            val localIHatY = iHatY * size
            val localIHatZ = iHatZ * size
            val localJHatX = jHatX * size
            val localJHatY = jHatY * size
            val localJHatZ = jHatZ * size

            position.load(particle)
            var x = position.getValue(0)
            var y = position.getValue(1)
            var z = position.getValue(2)
            if (previousPosition != null) {
                previousPosition.load(particle)
                x = ClientTickHandler.interpWorldPartialTicks(previousPosition.getValue(0), x)
                y = ClientTickHandler.interpWorldPartialTicks(previousPosition.getValue(1), y)
                z = ClientTickHandler.interpWorldPartialTicks(previousPosition.getValue(2), z)
            }

            color.load(particle)
            alphaMultiplier.load(particle)
            val r = color.getValue(0).toFloat()
            val g = color.getValue(1).toFloat()
            val b = color.getValue(2).toFloat()
            val a = color.getValue(3).toFloat() * alphaMultiplier.getValue(0).toFloat()

            vb.pos(x - localIHatX - localJHatX, y - localIHatY - localJHatY, z - localIHatZ - localJHatZ).tex(0.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x + localIHatX - localJHatX, y + localIHatY - localJHatY, z + localIHatZ - localJHatZ).tex(1.0, 0.0).color(r, g, b, a).endVertex()
            vb.pos(x + localIHatX + localJHatX, y + localIHatY + localJHatY, z + localIHatZ + localJHatZ).tex(1.0, 1.0).color(r, g, b, a).endVertex()
            vb.pos(x - localIHatX + localJHatX, y - localIHatY + localJHatY, z - localIHatZ + localJHatZ).tex(0.0, 1.0).color(r, g, b, a).endVertex()
        }

        tessellator.draw()

        //  GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        blendMode.reset()
        GlStateManager.enableCull()
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
    }
}