package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object BoolUniform: ShaderTest<BoolUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.primitive.set(true)
        shader.vector2.set(true, false)
        shader.vector3.set(true, false, true)
        shader.vector4.set(true, false, true, false)

        drawUnitQuad(matrix)
    }

    class Test: Shader("bool_tests", null, Identifier("liblib-albedo-test:shaders/bool_tests.frag")) {
        val primitive = Uniform.bool.create()
        val vector2 = Uniform.bvec2.create()
        val vector3 = Uniform.bvec3.create()
        val vector4 = Uniform.bvec4.create()
    }
}