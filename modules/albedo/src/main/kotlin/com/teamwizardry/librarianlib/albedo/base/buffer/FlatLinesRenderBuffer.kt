package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.FloatUniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.FloatVec2Uniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.Identifier
import java.lang.Math

public class FlatLinesRenderBuffer(vbo: VertexBuffer) :
    BaseRenderBuffer<FlatLinesRenderBuffer>(vbo, Primitive.LINES_ADJACENCY, Primitive.LINE_STRIP_ADJACENCY),
    ColorBuffer<FlatLinesRenderBuffer> {

    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val insetWidth: VertexLayoutElement =
        +VertexLayoutElement("InsetWidth", VertexLayoutElement.FloatFormat.FLOAT, 1, false)
    private val outsetWidth: VertexLayoutElement =
        +VertexLayoutElement("OutsetWidth", VertexLayoutElement.FloatFormat.FLOAT, 1, false)

    private val displaySize: FloatVec2Uniform = +Uniform.vec2.create("DisplaySize")

    init {
        bind(defaultShader)
    }

    public override fun color(r: Int, g: Int, b: Int, a: Int): FlatLinesRenderBuffer {
        start(color)
        putByte(r)
        putByte(g)
        putByte(b)
        putByte(a)
        return this
    }

    public fun inset(width: Float): FlatLinesRenderBuffer {
        start(insetWidth)
        putFloat(width)
        return this
    }

    public fun outset(width: Float): FlatLinesRenderBuffer {
        start(outsetWidth)
        putFloat(width)
        return this
    }

    /**
     * A shorthand for `inset(width/2).outset(width/2)`
     */
    public fun width(width: Float): FlatLinesRenderBuffer {
        start(insetWidth)
        putFloat(-width / 2)
        start(outsetWidth)
        putFloat(width / 2)
        return this
    }

    override fun setupState() {
        super.setupState()
        displaySize.set(Client.window.framebufferWidth.toFloat(), Client.window.framebufferHeight.toFloat())
    }

    public companion object {
        private val defaultShader: Shader = Shader.build("flat_lines")
            .vertex(Identifier("liblib-albedo:builtin/flat_lines.vert"))
            .geometry(Identifier("liblib-albedo:builtin/flat_lines.geom"))
            .fragment(Identifier("liblib-albedo:builtin/flat_lines.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatLinesRenderBuffer by lazy {
            FlatLinesRenderBuffer(VertexBuffer.SHARED)
        }
    }
}