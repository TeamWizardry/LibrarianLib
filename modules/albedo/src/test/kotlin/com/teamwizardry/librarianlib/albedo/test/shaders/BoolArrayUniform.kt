@file:Suppress("BooleanLiteralArgument")

package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.awt.Color

internal object BoolArrayUniform: ShaderTest<BoolArrayUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {

        val c = Color.WHITE

        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.primitive[0] = true
        shader.primitive[1] = false
        shader.vector2.set(0, true, false)
        shader.vector2.set(1, false, true)
        shader.vector3.set(0, true, false, true)
        shader.vector3.set(1, false, true, false)
        shader.vector4.set(0, true, false, true, false)
        shader.vector4.set(1, false, true, false, true)

        drawUnitQuad(matrix)

        val fr = Client.minecraft.textRenderer
        fr.draw(
            stack, "$index",
            (maxX - 2 - fr.getWidth("$index")).toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    class Test: Shader("bool_array_tests", null, Identifier("liblib-albedo-test:shaders/bool_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glBool[2]
        val vector2 = GLSL.bvec2[2]
        val vector3 = GLSL.bvec3[2]
        val vector4 = GLSL.bvec4[2]
    }
}