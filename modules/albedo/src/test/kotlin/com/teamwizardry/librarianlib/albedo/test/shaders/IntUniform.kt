package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
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
        val primitive = GLSL.glInt()
        val vector2 = GLSL.ivec2()
        val vector3 = GLSL.ivec3()
        val vector4 = GLSL.ivec4()
    }
}