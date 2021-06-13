package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.*
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

        rb.modelViewMatrix.set(RenderSystem.getModelViewMatrix())
        rb.projectionMatrix.set(RenderSystem.getProjectionMatrix())

        val m = Matrix4d()
        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(matrix, minX, maxY, 0).color(1f, 1f, 0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).color(1f, 1f, 1f, 1f).endVertex()

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

        rb.draw(GL11.GL_TRIANGLES)
        RenderSystem.enableTexture()
        RenderSystem.disableBlend()
    }

    class TestShader : Shader(
        "flat_color",
        Identifier("liblib-albedo-test:shaders/flat_color.vert"),
        Identifier("liblib-albedo-test:shaders/flat_color.frag")
    )

    class FlatColorRenderBuffer : RenderBuffer() {
        public val modelViewMatrix = +Uniform.mat4.create("ModelViewMatrix")
        public val projectionMatrix = +Uniform.mat4.create("ProjectionMatrix")

        private val _position = +VertexLayoutElement("Position", VertexLayoutElement.FloatFormat.FLOAT, 3, false)
        private val _color = +VertexLayoutElement("Color", VertexLayoutElement.FloatFormat.UNSIGNED_BYTE, 4, true)

        fun pos(matrix: Matrix4d, x: Int, y: Int, z: Int): FlatColorRenderBuffer {
            return pos(matrix, x.toDouble(), y.toDouble(), z.toDouble())
        }

        fun pos(matrix: Matrix4d, x: Double, y: Double, z: Double): FlatColorRenderBuffer {
            seek(_position)
            putFloat(matrix.transformX(x, y, z).toFloat())
            putFloat(matrix.transformY(x, y, z).toFloat())
            putFloat(matrix.transformZ(x, y, z).toFloat())
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