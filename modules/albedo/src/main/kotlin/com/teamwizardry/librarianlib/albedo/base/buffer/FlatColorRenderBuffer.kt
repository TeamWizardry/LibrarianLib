package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import net.minecraft.util.Identifier

public class FlatColorRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatColorRenderBuffer>(vbo),
    ColorBuffer<FlatColorRenderBuffer> {

    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

    init {
        bind(defaultShader)
    }

    public override fun color(r: Int, g: Int, b: Int, a: Int): FlatColorRenderBuffer {
        start(color)
        putByte(r)
        putByte(g)
        putByte(b)
        putByte(a)
        return this
    }

    public companion object {
        private val defaultShader: Shader = Shader.build("flat_color")
            .vertex(Identifier("liblib-albedo:builtin/flat_color.vert"))
            .fragment(Identifier("liblib-albedo:builtin/flat_color.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatColorRenderBuffer by lazy {
            FlatColorRenderBuffer(VertexBuffer.SHARED)
        }
    }
}
