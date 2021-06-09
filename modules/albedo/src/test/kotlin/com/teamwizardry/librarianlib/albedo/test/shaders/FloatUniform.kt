package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object FloatUniform: ShaderTest<FloatUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.primitive.set(10f)
        shader.vector2.set(10f, 20f)
        shader.vector3.set(10f, 20f, 30f)
        shader.vector4.set(10f, 20f, 30f, 40f)

        drawUnitQuad(matrix)
    }

    class Test: Shader("float_tests", null, Identifier("liblib-albedo-test:shaders/float_tests.frag")) {
        val primitive = GLSL.glFloat()
        val vector2 = GLSL.vec2()
        val vector3 = GLSL.vec3()
        val vector4 = GLSL.vec4()
    }
}