package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.awt.Color

internal object FloatArrayUniform: ShaderTest<FloatArrayUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.primitive[0] = 10f
        shader.primitive[1] = 20f
        shader.vector2.set(0, 10f, 20f)
        shader.vector2.set(1, 30f, 40f)
        shader.vector3.set(0, 10f, 20f, 30f)
        shader.vector3.set(1, 40f, 50f, 60f)
        shader.vector4.set(0, 10f, 20f, 30f, 40f)
        shader.vector4.set(1, 50f, 60f, 70f, 80f)

        drawUnitQuad(matrix)

        val fr = Client.minecraft.textRenderer
        fr.draw(
            stack, "$index",
            (maxX - 2 - fr.getWidth("$index")).toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    class Test: Shader("float_array_tests", null, Identifier("liblib-albedo-test:shaders/float_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glFloat[2]
        val vector2 = GLSL.vec2[2]
        val vector3 = GLSL.vec3[2]
        val vector4 = GLSL.vec4[2]
    }
}