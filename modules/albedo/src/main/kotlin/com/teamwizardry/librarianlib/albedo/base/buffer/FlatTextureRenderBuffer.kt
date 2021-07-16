package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import net.minecraft.util.Identifier

public class FlatTextureRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatTextureRenderBuffer>(vbo) {
    public val texture: SamplerUniform = +Uniform.sampler2D.create("Texture")
    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val texCoord: VertexLayoutElement =
        +VertexLayoutElement("TexCoord", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    public fun color(r: Float, g: Float, b: Float, a: Float): FlatTextureRenderBuffer {
        start(color)
        putByte((r * 255).toInt())
        putByte((g * 255).toInt())
        putByte((b * 255).toInt())
        putByte((a * 255).toInt())
        @Suppress("UNCHECKED_CAST")
        return this
    }

    public fun tex(u: Float, v: Float): FlatTextureRenderBuffer {
        start(texCoord)
        putFloat(u)
        putFloat(v)
        @Suppress("UNCHECKED_CAST")
        return this
    }

    public companion object {
        @JvmStatic
        public val defaultShader: Shader = Shader.build("flat_texture")
            .vertex(Identifier("liblib-albedo:builtin/flat_texture.vert"))
            .fragment(Identifier("liblib-albedo:builtin/flat_texture.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatTextureRenderBuffer by lazy {
            val buffer = FlatTextureRenderBuffer(VertexBuffer.SHARED)
            buffer.bind(defaultShader)
            buffer
        }
    }
}