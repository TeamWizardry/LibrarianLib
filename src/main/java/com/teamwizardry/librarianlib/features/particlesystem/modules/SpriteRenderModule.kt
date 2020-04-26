package com.teamwizardry.librarianlib.features.particlesystem.modules

import com.teamwizardry.librarianlib.core.client.ClientTickHandler
import com.teamwizardry.librarianlib.features.particlesystem.*
import com.teamwizardry.librarianlib.features.particlesystem.bindings.ConstantBinding
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import java.lang.IllegalArgumentException

/**
 * The bread-and-butter render module, a simple billboarded sprite.
 *
 * Particles are drawn as dynamically sized/colored sprites that are either billboarded or with an arbitrary facing
 * defined by [facingVector] (if any of facingVector's components are NaN the player's look vector will be used).
 * The particles are drawn as squares [size] blocks to a side and centered on the the particle's position.
 * One thing of note is that for some particle effects, particularly ones that should look consistent,
 * disabling interpolation by passing the current position for both [previousPosition] and [position] can make
 * the particles rock solid in their positions as opposed to jittering about slightly.
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
         * The OpenGL color of the particle
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
         * The alpha multiplier for the color. Defaults to 1 if not present.
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
         * The size of the sprite sheet (must be a power of 2)
         */
        @JvmField val spriteSheetSize: Int = 1,
        /**
         * The sprite index (indexed left-to-right, top-to-bottom)
         */
        @JvmField val spriteIndex: ReadParticleBinding = ConstantBinding(0.0)
) : ParticleRenderModule {
    init {
        previousPosition?.require(3)
        position.require(3)
        color.require(4)
        size.require(1)
        facingVector?.require(3)
        alphaMultiplier.require(1)
        spriteIndex.require(1)

        if(spriteSheetSize and (spriteSheetSize - 1) != 0) {
            throw IllegalArgumentException("Sprite sheet size $spriteSheetSize is not a power of 2")
        }
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

        var lookRightX = yawZ
        var lookRightY = 0.0
        var lookRightZ = -yawX

        var lookUpX = yawX * yawScale
        var lookUpY = pitchY
        var lookUpZ = yawZ * yawScale

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)

        var spriteSize = 1.0 / spriteSheetSize
        var spriteIndexMask = spriteSheetSize - 1
        var spriteSheetBits = MathHelper.log2(spriteSheetSize)

        particles.forEach { particle ->
            for (i in 0 until prepModules.size) {
                prepModules[i].update(particle)
            }
            var rightX = lookRightX
            var rightY = lookRightY
            var rightZ = lookRightZ

            var upX = lookUpX
            var upY = lookUpY
            var upZ = lookUpZ

            if (facingVector != null) {
                facingVector.load(particle)
                val facingX = facingVector.contents[0]
                val facingY = facingVector.contents[1]
                val facingZ = facingVector.contents[2]
                if(!facingX.isNaN() && !facingY.isNaN() && !facingZ.isNaN()) {
                    // x axis, facing • (0, 1, 0)
                    rightX = -facingZ
                    rightY = 0.0
                    rightZ = facingX
                    val rightInvLength = MathHelper.fastInvSqrt(rightX * rightX + rightY * rightY + rightZ * rightZ)
                    rightX *= -rightInvLength
                    rightY *= -rightInvLength
                    rightZ *= -rightInvLength

                    // y axis, facing • right
                    upX = facingY * facingX
                    upY = facingZ * -facingZ - facingX * facingX
                    upZ = facingY * facingZ
                    val upInvLength = MathHelper.fastInvSqrt(upX * upX + upY * upY + upZ * upZ)
                    upX *= -upInvLength
                    upY *= -upInvLength
                    upZ *= -upInvLength
                }
            }

            size.load(particle)
            val size = this.size.contents[0] / 2
            val localRightX = rightX * size
            val localRightY = rightY * size
            val localRightZ = rightZ * size
            val localUpX = upX * size
            val localUpY = upY * size
            val localUpZ = upZ * size

            position.load(particle)
            var x = position.contents[0]
            var y = position.contents[1]
            var z = position.contents[2]
            if(previousPosition != null) {
                previousPosition.load(particle)
                x = ClientTickHandler.interpWorldPartialTicks(previousPosition.contents[0], x)
                y = ClientTickHandler.interpWorldPartialTicks(previousPosition.contents[1], y)
                z = ClientTickHandler.interpWorldPartialTicks(previousPosition.contents[2], z)
            }

            color.load(particle)
            alphaMultiplier.load(particle)
            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            val a = color.contents[3].toFloat() * alphaMultiplier.contents[0].toFloat()

            var minU = 0.0
            var minV = 0.0
            var maxU = 1.0
            var maxV = 1.0

            if(spriteSheetSize > 1) {
                spriteIndex.load(particle)
                val index = spriteIndex.contents[0].toInt()
                val uIndex = index and spriteIndexMask
                val vIndex = index ushr spriteSheetBits
                minU = spriteSize * uIndex
                minV = spriteSize * vIndex
                maxU = spriteSize * (uIndex + 1)
                maxV = spriteSize * (vIndex + 1)
            }

            vb.pos(x - localRightX + localUpX, y - localRightY + localUpY, z - localRightZ + localUpZ).tex(minU, minV).color(r, g, b, a).endVertex()
            vb.pos(x + localRightX + localUpX, y + localRightY + localUpY, z + localRightZ + localUpZ).tex(maxU, minV).color(r, g, b, a).endVertex()
            vb.pos(x + localRightX - localUpX, y + localRightY - localUpY, z + localRightZ - localUpZ).tex(maxU, maxV).color(r, g, b, a).endVertex()
            vb.pos(x - localRightX - localUpX, y - localRightY - localUpY, z - localRightZ - localUpZ).tex(minU, maxV).color(r, g, b, a).endVertex()
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
