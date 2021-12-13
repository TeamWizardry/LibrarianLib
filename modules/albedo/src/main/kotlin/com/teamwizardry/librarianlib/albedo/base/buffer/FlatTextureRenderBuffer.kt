package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.SamplerUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import net.minecraft.util.Identifier
import java.awt.Color

public class FlatTextureRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatTextureRenderBuffer>(vbo) {
    public val texture: SamplerUniform = +Uniform.sampler2D.create("Texture")
    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val texCoord: VertexLayoutElement =
        +VertexLayoutElement("TexCoord", VertexLayoutElement.FloatFormat.FLOAT, 2, false)

    init {
        bind(defaultShader)
    }

    public fun color(r: Float, g: Float, b: Float, a: Float): FlatTextureRenderBuffer {
        start(color)
        putByte((r * 255).toInt())
        putByte((g * 255).toInt())
        putByte((b * 255).toInt())
        putByte((a * 255).toInt())
        return this
    }

    public fun color(r: Int, g: Int, b: Int, a: Int): FlatTextureRenderBuffer {
        start(color)
        putByte(r)
        putByte(g)
        putByte(b)
        putByte(a)
        return this
    }

    public fun color(color: Color): FlatTextureRenderBuffer {
        start(this.color)
        putByte(color.red)
        putByte(color.green)
        putByte(color.blue)
        putByte(color.alpha)
        return this
    }

    public fun tex(u: Float, v: Float): FlatTextureRenderBuffer {
        start(texCoord)
        putFloat(u)
        putFloat(v)
        return this
    }

    public fun tex(u: Double, v: Double): FlatTextureRenderBuffer = this.tex(u.toFloat(), v.toFloat())

    public companion object {
        private val defaultShader: Shader = Shader.build("flat_texture")
            .vertex(Identifier("liblib-albedo:builtin/flat_texture.vert"))
            .fragment(Identifier("liblib-albedo:builtin/flat_texture.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatTextureRenderBuffer by lazy {
            FlatTextureRenderBuffer(VertexBuffer.SHARED)
        }
    }
}