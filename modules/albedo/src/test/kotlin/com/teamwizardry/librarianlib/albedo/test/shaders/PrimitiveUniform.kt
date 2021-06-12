package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

object PrimitiveUniform: ShaderTest<PrimitiveUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.time.set(Client.time.seconds)

        drawUnitQuad(matrix)
    }

    class Test: Shader("primitive_uniform", Identifier("liblib-albedo-test:shaders/uniform_base.vert"), Identifier("liblib-albedo-test:shaders/primitive_uniform.frag")) {
        val time = Uniform.float.create("time")
    }
}

