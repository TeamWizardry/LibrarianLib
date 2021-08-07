package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.state.DefaultRenderStates
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.state.RenderState
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object SimpleRenderBuffer : ShaderTest() {

    val state = RenderState.normal.extend(DefaultRenderStates.Blend.DEFAULT)
    val shader by lazy {
        Shader.build("flat_color")
            .vertex(Identifier("liblib-albedo-test:flat_color.vert"))
            .fragment(Identifier("liblib-albedo-test:flat_color.frag"))
            .build()
    }
    var renderBuffer: TestRenderBuffer? = null

    override fun initialize() {
        val renderBuffer = TestRenderBuffer()
        renderBuffer.bind(shader)
        this.renderBuffer = renderBuffer
    }

    override fun delete() {
        renderBuffer?.delete()
        renderBuffer = null
    }

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val rb = FlatColorRenderBuffer.SHARED

        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(matrix, minX, maxY, 0).color(1f, 1f, 0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).color(1f, 1f, 1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).color(0f, 1f, 0f, 1f).endVertex()

        state.apply()
        rb.draw(Primitive.QUADS)
        state.cleanup()
    }

    class TestRenderBuffer : RenderBuffer(VertexBuffer.SHARED) {
        private val modelViewMatrix = +Uniform.mat4.create("ModelViewMatrix")
        private val projectionMatrix = +Uniform.mat4.create("ProjectionMatrix")

        private val position = +VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
        private val color = +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

        override fun setupState() {
            super.setupState()
            StandardUniforms.setModelViewMatrix(modelViewMatrix)
            StandardUniforms.setProjectionMatrix(projectionMatrix)
        }

        fun pos(matrix: Matrix4d, x: Int, y: Int, z: Int): TestRenderBuffer {
            return pos(matrix, x.toDouble(), y.toDouble(), z.toDouble())
        }

        fun pos(matrix: Matrix4d, x: Double, y: Double, z: Double): TestRenderBuffer {
            start(position)
            putFloat(matrix.transformX(x, y, z).toFloat())
            putFloat(matrix.transformY(x, y, z).toFloat())
            putFloat(matrix.transformZ(x, y, z).toFloat())
            return this
        }

        fun color(r: Float, g: Float, b: Float, a: Float): TestRenderBuffer {
            start(color)
            putByte((r * 255).toInt())
            putByte((g * 255).toInt())
            putByte((b * 255).toInt())
            putByte((a * 255).toInt())
            return this
        }
    }

}