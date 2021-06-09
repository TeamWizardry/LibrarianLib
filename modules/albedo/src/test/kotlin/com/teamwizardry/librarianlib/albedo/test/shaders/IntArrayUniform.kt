package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.awt.Color

internal object IntArrayUniform: ShaderTest<IntArrayUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.primitive[0] = 10
        shader.primitive[1] = 20
        shader.vector2.set(0, 10, 20)
        shader.vector2.set(1, 30, 40)
        shader.vector3.set(0, 10, 20, 30)
        shader.vector3.set(1, 40, 50, 60)
        shader.vector4.set(0, 10, 20, 30, 40)
        shader.vector4.set(1, 50, 60, 70, 80)

        drawUnitQuad(matrix)

        val fr = Client.minecraft.textRenderer
        fr.draw(
            stack, "$index",
            (maxX - 2 - fr.getWidth("$index")).toInt().toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    class Test: Shader("int_array_tests", null, Identifier("liblib-albedo-test:shaders/int_array_tests.frag")) {
        val index = Uniform.int.create()
        val primitive = Uniform.int.createArray(2)
        val vector2 = Uniform.ivec2.createArray(2)
        val vector3 = Uniform.ivec3.createArray(2)
        val vector4 = Uniform.ivec4.createArray(2)
    }
}