package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.StandardUniforms
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object SimpleRenderBuffer : ShaderTest() {

    val shader by lazy {
        Shader.build("flat_color")
            .vertex(Identifier("liblib-albedo-test:shaders/flat_color.vert"))
            .fragment(Identifier("liblib-albedo-test:shaders/flat_color.frag"))
            .build()
    }
    var renderBuffer: FlatColorRenderBuffer? = null

    override fun initialize() {
        val renderBuffer = FlatColorRenderBuffer()
        renderBuffer.bind(shader)
        this.renderBuffer = renderBuffer
    }

    override fun delete() {
        renderBuffer?.delete()
        renderBuffer = null
    }

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val vb = Tessellator.getInstance().buffer

        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        vb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)
        vb.vertex2d(matrix, minX, maxY).color(1f, 1f, 1f, 1f).next()
        vb.vertex2d(matrix, maxX, maxY).color(1f, 1f, 1f, 1f).next()
        vb.vertex2d(matrix, maxX, minY).color(1f, 1f, 1f, 1f).next()
        vb.vertex2d(matrix, minX, minY).color(1f, 1f, 1f, 1f).next()
        vb.end()
        BufferRenderer.draw(vb)

        val rb = renderBuffer ?: return

        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(matrix, minX, maxY, 0).color(1f, 1f, 0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).color(1f, 1f, 1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).color(0f, 1f, 0f, 1f).endVertex()

//        rb.pos(matrix, minX+15, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, minX+15, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, maxX+15, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()

//        rb.pos(matrix, maxX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, minX, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()
//
//        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, maxX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, maxX, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()
//
//        rb.pos(matrix, maxX, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, minX, maxY, 0).color(1f, 0f, 0f, 1f).endVertex()
//        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()

        rb.draw(Primitive.QUADS)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    class FlatColorRenderBuffer : RenderBuffer(VertexBuffer.SHARED) {
        private val modelViewMatrix = +Uniform.mat4.create("ModelViewMatrix")
        private val projectionMatrix = +Uniform.mat4.create("ProjectionMatrix")

        private val _position = +VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
        private val _color = +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

        override fun setupState() {
            super.setupState()
            StandardUniforms.setModelViewMatrix(modelViewMatrix)
            StandardUniforms.setProjectionMatrix(projectionMatrix)
        }

        fun pos(matrix: Matrix4d, x: Int, y: Int, z: Int): FlatColorRenderBuffer {
            return pos(matrix, x.toDouble(), y.toDouble(), z.toDouble())
        }

        fun pos(matrix: Matrix4d, x: Double, y: Double, z: Double): FlatColorRenderBuffer {
            start(_position)
            putFloat(matrix.transformX(x, y, z).toFloat())
            putFloat(matrix.transformY(x, y, z).toFloat())
            putFloat(matrix.transformZ(x, y, z).toFloat())
            return this
        }

        fun color(r: Float, g: Float, b: Float, a: Float): FlatColorRenderBuffer {
            start(_color)
            putByte((r * 255).toInt())
            putByte((g * 255).toInt())
            putByte((b * 255).toInt())
            putByte((a * 255).toInt())
            return this
        }
    }

}