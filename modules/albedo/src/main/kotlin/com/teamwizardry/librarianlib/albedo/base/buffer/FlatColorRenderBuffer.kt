package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import net.minecraft.util.Identifier
import java.awt.Color

public class FlatColorRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatColorRenderBuffer>(vbo) {
    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

    public fun color(r: Float, g: Float, b: Float, a: Float): FlatColorRenderBuffer {
        start(color)
        putByte((r * 255).toInt())
        putByte((g * 255).toInt())
        putByte((b * 255).toInt())
        putByte((a * 255).toInt())
        @Suppress("UNCHECKED_CAST")
        return this
    }

    public fun color(color: Color): FlatColorRenderBuffer {
        start(this.color)
        putByte(color.red)
        putByte(color.green)
        putByte(color.blue)
        putByte(color.alpha)
        @Suppress("UNCHECKED_CAST")
        return this
    }

    public companion object {
        @JvmStatic
        public val defaultShader: Shader = Shader.build("flat_color")
            .vertex(Identifier("liblib-albedo:builtin/flat_color.vert"))
            .fragment(Identifier("liblib-albedo:builtin/flat_color.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatColorRenderBuffer by lazy {
            val buffer = FlatColorRenderBuffer(VertexBuffer.SHARED)
            buffer.bind(defaultShader)
            buffer
        }
    }
}