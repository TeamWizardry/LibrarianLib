@file:Suppress("NOTHING_TO_INLINE")

package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix3f
import net.minecraft.util.math.Matrix4f

/**
 * Position + color + texture + normal + lightmap + fog
 */
public class ShadedTextureRenderBuffer(vbo: VertexBuffer, impl: BasicBufferImpl<ShadedTextureRenderBuffer>) :
    BasicRenderBuffer(vbo, impl),
    PositionBuffer<ShadedTextureRenderBuffer> by impl,
    ColorBuffer<ShadedTextureRenderBuffer> by impl,
    TexBuffer<ShadedTextureRenderBuffer> by impl,
    NormalBuffer<ShadedTextureRenderBuffer> by impl,
    LightmapBuffer<ShadedTextureRenderBuffer> by impl {

    public constructor(vbo: VertexBuffer) : this(
        vbo, BasicBufferImpl(enableTexture = true, enableNormal = true, enableLightmap = true, enableFog = true)
    )

    public companion object {
        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: ShadedTextureRenderBuffer by lazy {
            ShadedTextureRenderBuffer(VertexBuffer.SHARED)
        }
    }

    @JvmSynthetic
    public inline fun pos(x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return pos(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmSynthetic
    public inline fun pos(matrix: Matrix4d, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return pos(matrix, x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmSynthetic
    public inline fun pos(matrix: Matrix4f, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return pos(matrix, x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmSynthetic
    public inline fun pos(stack: MatrixStack, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return pos(stack, x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmSynthetic
    public inline fun normal(x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return normal(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmSynthetic
    public inline fun normal(matrix: Matrix3d, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return normal(matrix, x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmSynthetic
    public inline fun normal(matrix: Matrix3f, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return normal(matrix, x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmSynthetic
    public inline fun normal(stack: MatrixStack, x: Number, y: Number, z: Number): ShadedTextureRenderBuffer {
        return normal(stack, x.toFloat(), y.toFloat(), z.toFloat())
    }
}