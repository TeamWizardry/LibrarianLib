package com.teamwizardry.librarianlib.glitter.modules

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.core.bridge.IMatrix4f
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.rendering.BlendMode
import com.teamwizardry.librarianlib.core.util.kotlin.builder
import com.teamwizardry.librarianlib.core.util.mixinCast
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
import java.awt.Color
import java.lang.IllegalArgumentException

/**
 * The bread-and-butter render module, a simple billboarded sprite.
 *
 * Particles are drawn as dynamically sized/colored sprites that are either billboarded or with an arbitrary facing
 * defined by [facingVector] (if any of facingVector's components are NaN the player's look vector will be used).
 * The particles are drawn as rectangles [size] blocks to a side and centered on the the particle's position.
 * One thing of note is that for some particle effects, particularly ones that should look consistent,
 * disabling interpolation by passing the current position for both [previousPosition] and [position] can make
 * the particles rock solid in their positions as opposed to jittering about slightly.
 */
public class SpriteRenderModule private constructor(
    /**
     * The [RenderType] that is used to draw the particles. It should have both position and texture components.
     */
    public var renderType: RenderType,
    /**
     * The current position of the particle
     */
    public val position: ReadParticleBinding,
    /**
     * The position of the particle last tick, used to interpolate between ticks
     */
    public val previousPosition: ReadParticleBinding?,
    /**
     * The OpenGL color of the particle
     */
    public val color: ReadParticleBinding,
    /**
     * The width and height of the particle in meters. If this is a 1D binding the value is used for both width and
     * height, if it's a 2D binding the two axes are used as the width and height. Note that this does not affect UV
     * coordinates, so if you set this to non-square and have a square texture it will be distorted.
     */
    public val size: ReadParticleBinding,
    /**
     * If present, an artificial facing vector used instead of the player's look vector. This vector _does not need
     * to be normalized._
     */
    public val facingVector: ReadParticleBinding?,
    /**
     * The alpha multiplier for the color. Defaults to 1 if not present.
     */
    public val alphaMultiplier: ReadParticleBinding,
    /**
     * The size of the sprite sheet (must be a power of 2)
     */
    public val spriteSheetSize: Int,
    /**
     * The sprite index (indexed left-to-right, top-to-bottom)
     */
    public val spriteIndex: ReadParticleBinding,
    /**
     * If present, an artificial direction for the particle's "up" axis. This vector _does not need to be normalized._
     */
    public val upVector: ReadParticleBinding?,
    /**
     * If present, an artificial U/V texture size. When used in combination with sprite sheets, this will be a size
     * within the particle's specific sprite.
     */
    public val uvSize: ReadParticleBinding?,
    /**
     * If present, an offset to apply to the UV coordinates of the sprite. When used in combination with sprite sheets,
     * this will be an offset within the particle's specific sprite.
     */
    public val uvOffset: ReadParticleBinding?,
) : ParticleRenderModule {

    @Suppress("LocalVariableName")
    override fun render(
        matrixStack: MatrixStack,
        projectionMatrix: Matrix4f,
        particles: List<DoubleArray>,
        prepModules: List<ParticleUpdateModule>
    ) {
        val modelViewMatrix = matrixStack.last.matrix
        val buffer = Client.minecraft.renderTypeBuffers.bufferSource
        val builder = buffer.getBuffer(renderType)

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

        // `w = 0` means we won't apply translation when we use the matrix later
        val lookRightVec = Vector4f(-1f, 0f, 0f, 0f)
        val lookUpVec = Vector4f(0f, 1f, 0f, 0f)
        val lookVec = Vector4f(0f, 0f, 1f, 0f)

        val billboardedMatrix = modelViewMatrix.copy()
        val renderInfo = Client.minecraft.gameRenderer.activeRenderInfo

        val rotation = renderInfo.rotation.copy()
        rotation.multiply(-1f)
        billboardedMatrix.mul(rotation)

        lookRightVec.transform(billboardedMatrix)
        lookUpVec.transform(billboardedMatrix)
        lookVec.transform(billboardedMatrix)

        val lookRightX = 1.0 //lookRightVec.x.toDouble()
        val lookRightY = 0.0 //lookRightVec.y.toDouble()
        val lookRightZ = 0.0 //lookRightVec.z.toDouble()

        val lookUpX = 0.0 // lookUpVec.x.toDouble()
        val lookUpY = 1.0 // lookUpVec.y.toDouble()
        val lookUpZ = 0.0 // lookUpVec.z.toDouble()

        val cameraX = renderInfo.projectedView.x
        val cameraY = renderInfo.projectedView.y
        val cameraZ = renderInfo.projectedView.z

        val spriteSize = 1f / spriteSheetSize
        val spriteIndexMask = spriteSheetSize - 1
        val spriteSheetBits = MathHelper.log2(spriteSheetSize)

        // front-load this branch as a micro-optimization
        val widthSizeIndex: Int = if (this.size.contents.size == 2) 1 else 0

        particles.forEach { particle ->
            for (i in prepModules.indices) {
                prepModules[i].update(particle)
            }

            position.load(particle)
            var posX = position.contents[0]
            var posY = position.contents[1]
            var posZ = position.contents[2]
            if (previousPosition != null) {
                previousPosition.load(particle)
                posX = Client.worldTime.interp(previousPosition.contents[0], posX)
                posY = Client.worldTime.interp(previousPosition.contents[1], posY)
                posZ = Client.worldTime.interp(previousPosition.contents[2], posZ)
            }

            var rightX = lookRightX
            var rightY = lookRightY
            var rightZ = lookRightZ

            var upX = lookUpX
            var upY = lookUpY
            var upZ = lookUpZ

            // compute local axes
            if (facingVector != null || upVector != null) {
                var facingX = cameraX - posX
                var facingY = cameraY - posY
                var facingZ = cameraZ - posZ
                if (facingVector != null) {
                    facingVector.load(particle)
                    facingX = facingVector.contents[0]
                    facingY = facingVector.contents[1]
                    facingZ = facingVector.contents[2]
                }

                // cross product formula reference: c = a x b
                // cX = aY * bZ - aZ * bY
                // cY = aZ * bX - aX * bZ
                // cZ = aX * bY - aY * bX
                if (upVector == null) {
                    // if we have a facing vector and no up vector, compute the up and right vectors using the Y axis
                    // as a reference
                    if (!facingX.isNaN() && !facingY.isNaN() && !facingZ.isNaN()) {
                        if (facingX == 0.0 && facingZ == 0.0) {
                            rightX = 1.0
                            rightY = 0.0
                            rightZ = 0.0

                            upX = 0.0
                            upY = 0.0
                            upZ = 1.0
                        } else {

                            // note: these cross products don't care about the input normalization. The output magnitude
                            // will be mangled whether we normalize the inputs or not, so there's no point doing the
                            // excess calculations

                            // compute the rightward vector using the cross product `facing x (0, 1, 0)`
                            // the zeros can be simplified away, leaving us with essentially a 2d perpendicular vector
                            // on the xz plane (x, z) -> (z, -x)
                            rightX = facingZ
                            rightY = 0.0
                            rightZ = -facingX
                            // the Y axis will always be zero here, so we can do a 2d normalization
                            val rightInvLength = MathHelper.fastInvSqrt(rightX * rightX + rightZ * rightZ)
                            rightX *= rightInvLength
                            rightZ *= rightInvLength

                            // compute the upward vector using the cross product `facing x right`
                            // we can simplify some of the factors since `rightY` will always be zero
                            upX = facingY * rightZ
                            upY = facingZ * rightX - facingX * rightZ
                            upZ = -facingY * rightX
                            val upInvLength = MathHelper.fastInvSqrt(upX * upX + upY * upY + upZ * upZ)
                            upX *= upInvLength
                            upY *= upInvLength
                            upZ *= upInvLength
                        }
                    }
                } else {
                    // if we have an up vector, compute the right vector based upon it and the facing vector
                    // if no custom facing vector is specified, the facingXYZ variables will be toward the camera
                    upVector.load(particle)
                    upX = upVector.contents[0]
                    upY = upVector.contents[1]
                    upZ = upVector.contents[2]
                    val upInvLength = MathHelper.fastInvSqrt(upX * upX + upY * upY + upZ * upZ)
                    upX *= upInvLength
                    upY *= upInvLength
                    upZ *= upInvLength

                    // compute right axis using the cross product `facing x up`
                    rightX = facingY * upZ - facingZ * upY
                    rightY = facingZ * upX - facingX * upZ
                    rightZ = facingX * upY - facingY * upX
                    val rightInvLength = MathHelper.fastInvSqrt(rightX * rightX + rightY * rightY + rightZ * rightZ)
                    rightX *= -rightInvLength
                    rightY *= -rightInvLength
                    rightZ *= -rightInvLength
                }

                // both of those calculations spit out directions in world space. We need to transform those back into
                // the rendering space
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

            size.load(particle)
            val width = size.contents[0] / 2
            val height = size.contents[widthSizeIndex] / 2

            val localRightX = rightX * width
            val localRightY = rightY * width
            val localRightZ = rightZ * width
            val localUpX = upX * height
            val localUpY = upY * height
            val localUpZ = upZ * height

            val x = tm00 * posX + tm01 * posY + tm02 * posZ + tm03 * 1
            val y = tm10 * posX + tm11 * posY + tm12 * posZ + tm13 * 1
            val z = tm20 * posX + tm21 * posY + tm22 * posZ + tm23 * 1

            color.load(particle)
            alphaMultiplier.load(particle)
            val r = color.contents[0].toFloat()
            val g = color.contents[1].toFloat()
            val b = color.contents[2].toFloat()
            val a = color.contents[3].toFloat() * alphaMultiplier.contents[0].toFloat()

            var minU = 0f
            var minV = 0f
            var uSize = 1f
            var vSize = 1f

            if (spriteSheetSize > 1) {
                spriteIndex.load(particle)
                val index = spriteIndex.contents[0].toInt()
                val uIndex = index and spriteIndexMask
                val vIndex = index ushr spriteSheetBits
                minU = spriteSize * uIndex
                minV = spriteSize * vIndex
                uSize = spriteSize
                vSize = spriteSize
            }

            if(uvOffset != null) {
                uvOffset.load(particle)
                minU += uSize * uvOffset.contents[0].toFloat()
                minV += vSize * uvOffset.contents[1].toFloat()
            }
            if(uvSize != null) {
                uvSize.load(particle)
                uSize *= uvSize.contents[0].toFloat()
                vSize *= uvSize.contents[1].toFloat()
            }

            val maxU = minU + uSize
            val maxV = minV + vSize

            builder.pos(x - localRightX - localUpX, y - localRightY - localUpY, z - localRightZ - localUpZ)
                .color(r, g, b, a).tex(minU, maxV).endVertex()
            builder.pos(x + localRightX - localUpX, y + localRightY - localUpY, z + localRightZ - localUpZ)
                .color(r, g, b, a).tex(maxU, maxV).endVertex()
            builder.pos(x + localRightX + localUpX, y + localRightY + localUpY, z + localRightZ + localUpZ)
                .color(r, g, b, a).tex(maxU, minV).endVertex()
            builder.pos(x - localRightX + localUpX, y - localRightY + localUpY, z - localRightZ + localUpZ)
                .color(r, g, b, a).tex(minU, minV).endVertex()
        }

        buffer.finish()
    }

    public companion object {
        /**
         * @param renderType The [RenderType] that is used to draw the particles. It should have both position and
         * texture components. For simple render types use [simpleRenderType]
         * @param position The position binding for the particle (3D)
         */
        @JvmStatic
        public fun build(renderType: RenderType, position: ReadParticleBinding): Builder {
            return Builder(renderType, position)
        }

        /**
         * @param sprite The sprite texture to use. This defaults to normal blending and writing depth
         * @param position The position binding for the particle (3D)
         */
        @JvmStatic
        public fun build(sprite: ResourceLocation, position: ReadParticleBinding): Builder {
            return Builder(simpleRenderType(sprite), position)
        }

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
                "particle_type",
                DefaultVertexFormats.POSITION_COLOR_TEX,
                GL11.GL_QUADS,
                256,
                false,
                false,
                renderState.build(false)
            )
        }
    }

    /**
     * @param renderType The [RenderType] that is used to draw the particles. It should have both position and texture
     * components. For simple render types try [simpleRenderType]
     * @param position The position binding for the particle (3D)
     */
    public class Builder(private val renderType: RenderType, private val position: ReadParticleBinding) {
        init {
            position.require(3)
        }

        private var previousPosition: ReadParticleBinding? = null
        private var color: ReadParticleBinding = ConstantBinding(1.0, 1.0, 1.0, 1.0)
        private var size: ReadParticleBinding = ConstantBinding(1.0)
        private var facingVector: ReadParticleBinding? = null
        private var upVector: ReadParticleBinding? = null
        private var alphaMultiplier: ReadParticleBinding = ConstantBinding(1.0)
        private var spriteSheetSize: Int = 1
        private var spriteIndex: ReadParticleBinding = ConstantBinding(0.0)
        private var uvSize: ReadParticleBinding? = null
        private var uvOffset: ReadParticleBinding? = null

        /**
         * The position of the particle last tick, used to interpolate between ticks
         */
        public fun previousPosition(value: ReadParticleBinding): Builder = builder {
            value.require(3)
            previousPosition = value
        }

        /**
         * The tint color of the particle
         */
        public fun color(value: ReadParticleBinding): Builder = builder {
            value.require(4)
            color = value
        }

        /**
         * The tint color of the particle
         */
        public fun color(value: Color): Builder = builder {
            color = ConstantBinding(value.red / 255.0, value.green / 255.0, value.blue / 255.0, value.alpha / 255.0)
        }

        /**
         * The tint color of the particle
         */
        public fun color(red: Double, green: Double, blue: Double, alpha: Double): Builder = builder {
            color = ConstantBinding(red, green, blue, alpha)
        }

        /**
         * The width and height of the particle in meters. If this is a 1D binding the value is used for both width and
         * height, if it's a 2D binding the two axes are used as the width and height. Note that this does not affect UV
         * coordinates, so if you set this to non-square and have a square texture it will be distorted.
         */
        public fun size(value: ReadParticleBinding): Builder = builder {
            value.require(1, 2)
            size = value
        }

        /**
         * The size of the particle in meters.
         */
        public fun size(value: Double): Builder = builder {
            size = ConstantBinding(value)
        }

        /**
         * The width and height of the particle in meters. Note that this does not affect UV coordinates, so if you set
         * this to non-square and have a square texture it will be distorted.
         */
        public fun size(width: Double, height: Double): Builder = builder {
            size = ConstantBinding(width, height)
        }

        /**
         * If present, an artificial facing vector used instead of the player's look vector. This vector _does not need
         * to be normalized._
         */
        public fun facingVector(value: ReadParticleBinding): Builder = builder {
            value.require(3)
            facingVector = value
        }

        /**
         * If present, an artificial direction for the particle's "up" axis. This vector _does not need to be normalized._
         */
        public fun upVector(value: ReadParticleBinding): Builder = builder {
            value.require(3)
            upVector = value
        }

        /**
         * The alpha multiplier for the color. Defaults to 1 if not present.
         */
        public fun alphaMultiplier(value: ReadParticleBinding): Builder = builder {
            value.require(1)
            alphaMultiplier = value
        }

        /**
         * Configures the sprite sheet.
         *
         * @param size The size of the sprite sheet (must be a power of 2)
         * @param index A binding for the sprite index (indexed left-to-right, top-to-bottom)
         */
        public fun spriteSheet(size: Int, index: ReadParticleBinding): Builder = builder {
            if (size and (size - 1) != 0) {
                throw IllegalArgumentException("Sprite sheet size $size is not a power of 2")
            }
            spriteSheetSize = size
            index.require(1)
            spriteIndex = index
        }

        /**
         * If present, an artificial U/V texture size. When used in combination with sprite sheets, this will be a size
         * within the particle's specific sprite.
         */
        public fun uvSize(value: ReadParticleBinding): Builder = builder {
            value.require(2)
            uvSize = value
        }

        /**
         * If present, an offset to apply to the UV coordinates of the sprite. When used in combination with sprite sheets,
         * this will be an offset within the particle's specific sprite.
         */
        public fun uvOffset(value: ReadParticleBinding): Builder = builder {
            value.require(2)
            uvOffset = value
        }

        public fun build(): SpriteRenderModule {
            return SpriteRenderModule(
                renderType,
                position,
                previousPosition,
                color,
                size,
                facingVector,
                alphaMultiplier,
                spriteSheetSize,
                spriteIndex,
                upVector,
                uvSize,
                uvOffset
            )
        }
    }
}
