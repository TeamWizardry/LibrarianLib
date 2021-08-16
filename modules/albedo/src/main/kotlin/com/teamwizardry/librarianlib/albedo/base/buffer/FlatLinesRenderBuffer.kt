package com.teamwizardry.librarianlib.albedo.base.buffer

import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.FloatVec2Uniform
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import net.minecraft.util.Identifier
import java.awt.Color

public class FlatLinesRenderBuffer(vbo: VertexBuffer) : BaseRenderBuffer<FlatLinesRenderBuffer>(vbo) {
    private val color: VertexLayoutElement =
        +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)
    private val insetWidth: VertexLayoutElement =
        +VertexLayoutElement("InsetWidth", VertexLayoutElement.FloatFormat.FLOAT, 1, false)
    private val outsetWidth: VertexLayoutElement =
        +VertexLayoutElement("OutsetWidth", VertexLayoutElement.FloatFormat.FLOAT, 1, false)

    private val displaySize: FloatVec2Uniform = +Uniform.vec2.create("DisplaySize")

    public fun color(r: Float, g: Float, b: Float, a: Float): FlatLinesRenderBuffer {
        start(color)
        putByte((r * 255).toInt())
        putByte((g * 255).toInt())
        putByte((b * 255).toInt())
        putByte((a * 255).toInt())
        return this
    }

    public fun color(color: Color): FlatLinesRenderBuffer {
        start(this.color)
        putByte(color.red)
        putByte(color.green)
        putByte(color.blue)
        putByte(color.alpha)
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
        putFloat(width/2)
        start(outsetWidth)
        putFloat(width/2)
        return this
    }

    override fun setupState() {
        super.setupState()
        displaySize.set(Client.window.framebufferWidth.toFloat(), Client.window.framebufferHeight.toFloat())
    }

    public companion object {
        @JvmStatic
        public val defaultShader: Shader = Shader.build("flat_lines")
            .vertex(Identifier("liblib-albedo:builtin/flat_lines.vert"))
            .geometry(Identifier("liblib-albedo:builtin/flat_lines.geom"))
            .fragment(Identifier("liblib-albedo:builtin/flat_lines.frag"))
            .build()

        @JvmStatic
        @get:JvmName("getShared")
        public val SHARED: FlatLinesRenderBuffer by lazy {
            val buffer = FlatLinesRenderBuffer(VertexBuffer.SHARED)
            buffer.bind(defaultShader)
            buffer
        }
    }
}