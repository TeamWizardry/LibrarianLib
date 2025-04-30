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

internal object TestBoolArrayUniform : ShaderTest() {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        val rb = TestBuffer.SHARED
        rb.index.set(floorInt(Client.time.seconds % 2))
        rb.primitive[0] = true
        rb.primitive[1] = false
        rb.vector2.set(0, true, false)
        rb.vector2.set(1, false, true)
        rb.vector3.set(0, true, false, true)
        rb.vector3.set(1, false, true, false)
        rb.vector4.set(0, true, false, true, false)
        rb.vector4.set(1, false, true, false, true)
        rb.pos(matrix, minX, minY, 0).uv(0f, 0f).endVertex()
        rb.pos(matrix, minX, maxY, 0).uv(0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).uv(1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).uv(1f, 0f).endVertex()

        rb.draw(Primitive.QUADS)
    }

    private class TestBuffer(vbo: VertexBuffer) : BaseRenderBuffer<TestBuffer>(vbo) {
        val index = +Uniform.int.create("index")
        val primitive = +Uniform.bool.createArray("primitive", 2)
        val vector2 = +Uniform.bvec2.createArray("vector2", 2)
        val vector3 = +Uniform.bvec3.createArray("vector3", 2)
        val vector4 = +Uniform.bvec4.createArray("vector4", 2)

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
            private val defaultShader: Shader = Shader.build("bool_array_tests")
                .vertex(Identifier.of("liblib-albedo-test:uniform_base.vert"))
                .fragment(Identifier.of("liblib-albedo-test:bool_array_tests.frag"))
                .build()

            val SHARED: TestBuffer by lazy {
                TestBuffer(VertexBuffer.SHARED)
            }
        }
    }
}

