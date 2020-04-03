package com.teamwizardry.librarianlib.particles.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.platform.GlStateManager
import com.teamwizardry.librarianlib.core.bridge.IMatrix3f
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.particles.BlendMode
import com.teamwizardry.librarianlib.particles.ParticleRenderModule
import com.teamwizardry.librarianlib.particles.ParticleUpdateModule
import com.teamwizardry.librarianlib.particles.ReadParticleBinding
import com.teamwizardry.librarianlib.particles.bindings.ConstantBinding
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.client.renderer.Vector4f
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper

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
    @JvmField val enableBlend: Boolean = true
) : ParticleRenderModule {
    init {
        previousPosition?.require(3)
        position.require(3)
        color.require(4)
        size.require(1)
        facingVector?.require(3)
        alphaMultiplier.require(1)
    }

    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
//        Minecraft.getInstance().textureManager.bindTexture(sprite)

        val modelViewMatrix = matrixStack.last.matrix
        val buffer = Client.minecraft.renderTypeBuffers.bufferSource
        val builder = buffer.getBuffer(SpriteRenderType.spriteRenderType(sprite))

        val transformMatrix = matrixStack.last.matrix as IMatrix4f
        val normalMatrix = matrixStack.last.normal as IMatrix3f

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

        val nm00 = normalMatrix.m00
        val nm01 = normalMatrix.m01
        val nm02 = normalMatrix.m02
        val nm10 = normalMatrix.m10
        val nm11 = normalMatrix.m11
        val nm12 = normalMatrix.m12
        val nm20 = normalMatrix.m20
        val nm21 = normalMatrix.m21
        val nm22 = normalMatrix.m22

        GlStateManager.enableTexture()
        if (enableBlend) {
            GlStateManager.enableBlend()
        } else {
            GlStateManager.disableBlend()
        }

        blendMode.glApply()

        val lookRightVec = Vector4f(-1f, 0f, 0f, 0f)
        val lookUpVec = Vector4f(0f, 1f, 0f, 0f)

        val billboardedMatrix = modelViewMatrix.copy()
        val renderInfo = Client.minecraft.gameRenderer.activeRenderInfo
        val rotation = renderInfo.rotation.copy()
        rotation.multiply(-1f)
        billboardedMatrix.mul(rotation)

        lookRightVec.transform(billboardedMatrix)
        lookUpVec.transform(billboardedMatrix)

        val lookRightX = lookRightVec.x.toDouble()
        val lookRightY = lookRightVec.y.toDouble()
        val lookRightZ = lookRightVec.z.toDouble()

        val lookUpX = lookUpVec.x.toDouble()
        val lookUpY = lookUpVec.y.toDouble()
        val lookUpZ = lookUpVec.z.toDouble()

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


                    rightX = nm00 * rightX + nm01 * rightY + nm02 * rightZ
                    rightY = nm10 * rightX + nm11 * rightY + nm12 * rightZ
                    rightZ = nm20 * rightX + nm21 * rightY + nm22 * rightZ
                    upX = nm00 * upX + nm01 * upY + nm02 * upZ
                    upY = nm10 * upX + nm11 * upY + nm12 * upZ
                    upZ = nm20 * upX + nm21 * upY + nm22 * upZ
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
                x = Client.worldTime.interp(previousPosition.contents[0], x)
                y = Client.worldTime.interp(previousPosition.contents[1], y)
                z = Client.worldTime.interp(previousPosition.contents[2], z)
            }

            val x1 = x
            val y1 = y
            val z1 = z
            x = tm00 * x1 + tm01 * y1 + tm02 * z1 + tm03 * 1
            y = tm10 * x1 + tm11 * y1 + tm12 * z1 + tm13 * 1
            z = tm20 * x1 + tm21 * y1 + tm22 * z1 + tm23 * 1

            color.load(particle)
            alphaMultiplier.load(particle)
            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            val a = color.contents[3].toFloat() * alphaMultiplier.contents[0].toFloat()

            builder.pos(x - localRightX - localUpX, y - localRightY - localUpY, z - localRightZ - localUpZ).color(r, g, b, a).tex(0f, 1f).endVertex()
            builder.pos(x + localRightX - localUpX, y + localRightY - localUpY, z + localRightZ - localUpZ).color(r, g, b, a).tex(1f, 1f).endVertex()
            builder.pos(x + localRightX + localUpX, y + localRightY + localUpY, z + localRightZ + localUpZ).color(r, g, b, a).tex(1f, 0f).endVertex()
            builder.pos(x - localRightX + localUpX, y - localRightY + localUpY, z - localRightZ + localUpZ).color(r, g, b, a).tex(0f, 0f).endVertex()
        }

        buffer.finish()

        blendMode.reset()
    }
}
