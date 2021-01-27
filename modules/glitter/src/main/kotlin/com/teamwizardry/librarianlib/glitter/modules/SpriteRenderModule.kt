package com.teamwizardry.librarianlib.glitter.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.glitter.ParticleRenderModule
import com.teamwizardry.librarianlib.glitter.ParticleUpdateModule
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.math.vector.Vector4f
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
public class SpriteRenderModule @JvmOverloads constructor(
    /**
     * The [RenderType] that is used to draw the particles. It should have both position and texture components.
     */
    @JvmField public var renderType: RenderType,
    /**
     * The current position of the particle
     */
    @JvmField public val position: ReadParticleBinding,
    /**
     * The position of the particle last tick, used to interpolate between ticks
     */
    @JvmField public val previousPosition: ReadParticleBinding? = null,
    /**
     * The OpenGL color of the particle
     */
    @JvmField public val color: ReadParticleBinding = ConstantBinding(1.0, 1.0, 1.0, 1.0),
    /**
     * The width and height of the particle in meters
     */
    @JvmField public val size: ReadParticleBinding = ConstantBinding(1.0),
    /**
     * If present, an artificial facing vector used instead of the player's look vector. This vector _does not need
     * to be normalized._
     */
    @JvmField public val facingVector: ReadParticleBinding? = null,
    /**
     * The alpha multiplier for the color. Defaults to 1 if not present.
     */
    @JvmField public val alphaMultiplier: ReadParticleBinding = ConstantBinding(1.0),
    /**
     * The size of the sprite sheet (must be a power of 2)
     */
    @JvmField public val spriteSheetSize: Int = 1,
    /**
     * The sprite index (indexed left-to-right, top-to-bottom)
     */
    @JvmField public val spriteIndex: ReadParticleBinding = ConstantBinding(0.0)
): ParticleRenderModule {
    init {
        previousPosition?.require(3)
        position.require(3)
        color.require(4)
        size.require(1)
        facingVector?.require(3)
        alphaMultiplier.require(1)
        spriteIndex.require(1)

        if (spriteSheetSize and (spriteSheetSize - 1) != 0) {
            throw IllegalArgumentException("Sprite sheet size $spriteSheetSize is not a power of 2")
        }
    }

    @Suppress("CAST_NEVER_SUCCEEDS", "LocalVariableName")
    override fun render(matrixStack: MatrixStack, projectionMatrix: Matrix4f, particles: List<DoubleArray>, prepModules: List<ParticleUpdateModule>) {
        val modelViewMatrix = matrixStack.last.matrix
        val buffer = Client.minecraft.renderTypeBuffers.bufferSource
        val builder = buffer.getBuffer(renderType)

        val transformMatrix = modelViewMatrix as IMatrix4f
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

        // `w = 0` means we won't apply translation when we use the matrix later
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

        val spriteSize = 1f / spriteSheetSize
        val spriteIndexMask = spriteSheetSize - 1
        val spriteSheetBits = MathHelper.log2(spriteSheetSize)

        particles.forEach { particle ->
            for (i in prepModules.indices) {
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
                if (!facingX.isNaN() && !facingY.isNaN() && !facingZ.isNaN()) {
                    // x axis, facing • (0, 1, 0)
                    if (facingX == 0.0 && facingZ == 0.0) {
                        rightX = 1.0
                        rightY = 0.0
                        rightZ = 0.0

                        upX = 0.0
                        upY = 0.0
                        upZ = 1.0
                    } else {
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

                    val _rightX = rightX
                    val _rightY = rightY
                    val _rightZ = rightZ
                    rightX = tm00 * _rightX + tm01 * _rightY + tm02 * _rightZ
                    rightY = tm10 * _rightX + tm11 * _rightY + tm12 * _rightZ
                    rightZ = tm20 * _rightX + tm21 * _rightY + tm22 * _rightZ

                    val _upX = upX
                    val _upY = upY
                    val _upZ = upZ
                    upX = tm00 * _upX + tm01 * _upY + tm02 * _upZ
                    upY = tm10 * _upX + tm11 * _upY + tm12 * _upZ
                    upZ = tm20 * _upX + tm21 * _upY + tm22 * _upZ
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
            if (previousPosition != null) {
                previousPosition.load(particle)
                x = Client.worldTime.interp(previousPosition.contents[0], x)
                y = Client.worldTime.interp(previousPosition.contents[1], y)
                z = Client.worldTime.interp(previousPosition.contents[2], z)
            }

            val _x = x
            val _y = y
            val _z = z
            x = tm00 * _x + tm01 * _y + tm02 * _z + tm03 * 1
            y = tm10 * _x + tm11 * _y + tm12 * _z + tm13 * 1
            z = tm20 * _x + tm21 * _y + tm22 * _z + tm23 * 1

            color.load(particle)
            alphaMultiplier.load(particle)
            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            val a = color.contents[3].toFloat() * alphaMultiplier.contents[0].toFloat()

            var minU = 0f
            var minV = 0f
            var maxU = 1f
            var maxV = 1f

            if (spriteSheetSize > 1) {
                spriteIndex.load(particle)
                val index = spriteIndex.contents[0].toInt()
                val uIndex = index and spriteIndexMask
                val vIndex = index ushr spriteSheetBits
                minU = spriteSize * uIndex
                minV = spriteSize * vIndex
                maxU = spriteSize * (uIndex + 1)
                maxV = spriteSize * (vIndex + 1)
            }

            builder.pos(x - localRightX - localUpX, y - localRightY - localUpY, z - localRightZ - localUpZ).color(r, g, b, a).tex(minU, maxV).endVertex()
            builder.pos(x + localRightX - localUpX, y + localRightY - localUpY, z + localRightZ - localUpZ).color(r, g, b, a).tex(maxU, maxV).endVertex()
            builder.pos(x + localRightX + localUpX, y + localRightY + localUpY, z + localRightZ + localUpZ).color(r, g, b, a).tex(maxU, minV).endVertex()
            builder.pos(x - localRightX + localUpX, y - localRightY + localUpY, z - localRightZ + localUpZ).color(r, g, b, a).tex(minU, minV).endVertex()
        }

        buffer.finish()
    }

    public companion object {
        @JvmStatic
        @JvmOverloads
        public fun simpleRenderType(
            /**
             * The sprite texture to use
             */
            sprite: ResourceLocation,
            /**
             * The OpenGL source/dest enableBlend factors. A null value disables blending.
             */
            blendMode: BlendMode? = BlendMode.NORMAL,
            /**
             * Whether to write to the depth buffer
             */
            writeDepth: Boolean = true,
            /**
             * Whether to blur the texture
             */
            blur: Boolean = false
        ): RenderType {

            val renderState = RenderType.State.getBuilder()
                .texture(RenderState.TextureState(sprite, blur, false))
                .cull(DefaultRenderStates.CULL_DISABLED)
                .alpha(DefaultRenderStates.DEFAULT_ALPHA)

            if (blendMode != null) {
                renderState.transparency(RenderState.TransparencyState("particle_transparency", {
                    RenderSystem.enableBlend()
                    blendMode.glApply()
                }, {
                    RenderSystem.disableBlend()
                    RenderSystem.defaultBlendFunc()
                }))
            }

            if (!writeDepth) {
                renderState.writeMask(DefaultRenderStates.COLOR_WRITE)
            }

            @Suppress("INACCESSIBLE_TYPE")
            return RenderType.makeType(
                "particle_type", DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState.build(false)
            )
        }
    }
}
