package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object IntUniform: ShaderTest<IntUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.primitive.set(10)
        shader.vector2.set(10, 20)
        shader.vector3.set(10, 20, 30)
        shader.vector4.set(10, 20, 30, 40)

        drawUnitQuad(matrix)
    }

    class Test: Shader("int_tests", null, Identifier("liblib-albedo-test:shaders/int_tests.frag")) {
        val primitive = Uniform.int.create()
        val vector2 = Uniform.ivec2.create()
        val vector3 = Uniform.ivec3.create()
        val vector4 = Uniform.ivec4.create()
    }
}