package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.uniform.GLSLStruct
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11

internal object SimpleRenderBuffer : ShaderTest<SimpleRenderBuffer.TestShader>() {

    var renderBuffer: FlatColorRenderBuffer? = null

    override fun initialize() {
        super.initialize()
        val renderBuffer = FlatColorRenderBuffer()
        renderBuffer.link(shader)
        renderBuffer.setupVAO()
        this.renderBuffer = renderBuffer
    }

    override fun delete() {
        super.delete()
        renderBuffer?.delete()
        renderBuffer = null
    }

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val rb = renderBuffer ?: return

        rb.modelViewMatrix.set(RenderSystem.getModelViewMatrix())
        rb.projectionMatrix.set(RenderSystem.getProjectionMatrix())

        val s = 10f
        rb.pos(-s, s,  0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(s,  s,  0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(s,  -s, 0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(-s, -s, 0f).color(1f, 0f, 0f, 1f).endVertex()

        rb.pos(-s, -s, 0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(s,  -s, 0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(s,  s,  0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(-s, s,  0f).color(1f, 0f, 0f, 1f).endVertex()
        rb.draw(GL11.GL_QUADS)
    }

    class TestShader : Shader(
        "flat_color",
        Identifier("liblib-albedo-test:shaders/flat_color.vert"),
        Identifier("liblib-albedo-test:shaders/flat_color.frag")
    )

    class FlatColorRenderBuffer : RenderBuffer() {
        public val modelViewMatrix = +Uniform.mat4.create("ModelViewMatrix", true)
        public val projectionMatrix = +Uniform.mat4.create("ProjectionMatrix", true)

        private val _position = +VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
        private val _color = +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

        fun pos(x: Float, y: Float, z: Float): FlatColorRenderBuffer {
            seek(_position)
            putFloat(x)
            putFloat(y)
            putFloat(z)
            return this
        }

        fun color(r: Float, g: Float, b: Float, a: Float): FlatColorRenderBuffer {
            seek(_color)
            putByte((r * 255).toInt())
            putByte((g * 255).toInt())
            putByte((b * 255).toInt())
            putByte((a * 255).toInt())
            return this
        }
    }
}