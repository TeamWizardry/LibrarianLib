package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.base.buffer.BaseRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.floorInt
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object TestIntArrayUniform : ShaderTest() {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        val rb = TestBuffer.SHARED
        rb.index.set(floorInt(Client.time.seconds % 2))
        rb.primitive[0] = 10
        rb.primitive[1] = 20
        rb.vector2.set(0, 10, 20)
        rb.vector2.set(1, 30, 40)
        rb.vector3.set(0, 10, 20, 30)
        rb.vector3.set(1, 40, 50, 60)
        rb.vector4.set(0, 10, 20, 30, 40)
        rb.vector4.set(1, 50, 60, 70, 80)
        rb.pos(matrix, minX, minY, 0).uv(0f, 0f).endVertex()
        rb.pos(matrix, minX, maxY, 0).uv(0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).uv(1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).uv(1f, 0f).endVertex()

        rb.draw(Primitive.QUADS)
    }

    private class TestBuffer(vbo: VertexBuffer) : BaseRenderBuffer<TestBuffer>(vbo) {
        val index = +Uniform.int.create("index")
        val primitive = +Uniform.int.createArray("primitive", 2)
        val vector2 = +Uniform.ivec2.createArray("vector2", 2)
        val vector3 = +Uniform.ivec3.createArray("vector3", 2)
        val vector4 = +Uniform.ivec4.createArray("vector4", 2)

        private val uv = +VertexLayoutElement("UV", VertexLayoutElement.FloatFormat.FLOAT, 2, true)

        init {
            bind(defaultShader)
        }

        fun uv(u: Float, v: Float): TestBuffer {
            start(uv)
            putFloat(u)
            putFloat(v)
            return this
        }

        companion object {
            private val defaultShader: Shader = Shader.build("int_array_tests")
                .vertex(Identifier.of("liblib_albedo_test:uniform_base.vert"))
                .fragment(Identifier.of("liblib_albedo_test:int_array_tests.frag"))
                .build()

            val SHARED: TestBuffer by lazy {
                TestBuffer(VertexBuffer.SHARED)
            }
        }
    }
}

