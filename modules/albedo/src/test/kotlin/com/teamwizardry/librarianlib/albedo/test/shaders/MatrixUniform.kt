package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.awt.Color

internal object MatrixUniform: ShaderTest<MatrixUniform.Test>(
    minX = -8,
    minY = -8,
    maxX = 136,
    maxY = 136
) {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val matrixType = (Client.time.seconds % 3).toInt()

        val mat4Label: String = when (matrixType) {
            0 -> {
                shader.matrix4x4.set(
                    00f, 10f, 20f, 30f, // column 0
                    01f, 11f, 21f, 31f, // column 1
                    02f, 12f, 22f, 32f, // column 2
                    03f, 13f, 23f, 33f  // column 3
                )
                "direct"
            }
            1 -> {
                shader.matrix4x4.set(Matrix4d(
                    00.0, 01.0, 02.0, 03.0,
                    10.0, 11.0, 12.0, 13.0,
                    20.0, 21.0, 22.0, 23.0,
                    30.0, 31.0, 32.0, 33.0
                ))
                "Matrix4d"
            }
            else -> {
                shader.matrix4x4.set(Matrix4d(
                    00f, 01f, 02f, 03f,
                    10f, 11f, 12f, 13f,
                    20f, 21f, 22f, 23f,
                    30f, 31f, 32f, 33f
                ).toMatrix4f())
                "Matrix4f"
            }
        }
        shader.matrix4x3.set(
            00f, 10f, 20f, // column 0
            01f, 11f, 21f, // column 1
            02f, 12f, 22f, // column 2
            03f, 13f, 23f  // column 3
        )
        shader.matrix4x2.set(
            00f, 10f, // column 0
            01f, 11f, // column 1
            02f, 12f, // column 2
            03f, 13f  // column 3
        )

        shader.matrix3x4.set(
            00f, 10f, 20f, 30f, // column 0
            01f, 11f, 21f, 31f, // column 1
            02f, 12f, 22f, 32f  // column 2
        )

        val mat3Label: String = when(matrixType) {
            0 -> {
                shader.matrix3x3.set(
                    00f, 10f, 20f, // column 0
                    01f, 11f, 21f, // column 1
                    02f, 12f, 22f  // column 2
                )
                "direct"
            }
            1 -> {
                shader.matrix3x3.set(Matrix3d(
                    00.0, 01.0, 02.0,
                    10.0, 11.0, 12.0,
                    20.0, 21.0, 22.0
                ))
                "Matrix3d"
            }
            else -> {
                shader.matrix3x3.set(Matrix3d(
                    00.0, 01.0, 02.0,
                    10.0, 11.0, 12.0,
                    20.0, 21.0, 22.0
                ).toMatrix3f())
                "Matrix3f"
            }
        }
        shader.matrix3x2.set(
            00f, 10f, // column 0
            01f, 11f, // column 1
            02f, 12f  // column 2
        )

        shader.matrix2x4.set(
            00f, 10f, 20f, 30f, // column 0
            01f, 11f, 21f, 31f  // column 1
        )
        shader.matrix2x3.set(
            00f, 10f, 20f, // column 0
            01f, 11f, 21f  // column 1
        )
        shader.matrix2x2.set(
            00f, 10f, // column 0
            01f, 11f  // column 1
        )

        drawUnitQuad(matrix)

        val fr = Client.minecraft.textRenderer
        val cellSize = 16
        fr.draw(
            stack, mat4Label,
            (minX + cellSize * 2 - fr.getWidth(mat4Label)/2).toInt().toFloat(),
            (minY + cellSize * 2 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )
        fr.draw(
            stack, mat3Label,
            (minX + cellSize * 5.5 - fr.getWidth(mat3Label)/2).toInt().toFloat(),
            (minY + cellSize * 5.5 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )

    }

    class Test: Shader("matrix_tests", Identifier("liblib-albedo-test:shaders/uniform_base.vert"), Identifier("liblib-albedo-test:shaders/matrix_tests.frag")) {
        val matrix4x4 = Uniform.mat4.create("matrix4x4")
        val matrix4x3 = Uniform.mat4x3.create("matrix4x3")
        val matrix4x2 = Uniform.mat4x2.create("matrix4x2")

        val matrix3x4 = Uniform.mat3x4.create("matrix3x4")
        val matrix3x3 = Uniform.mat3.create("matrix3x3")
        val matrix3x2 = Uniform.mat3x2.create("matrix3x2")

        val matrix2x4 = Uniform.mat2x4.create("matrix2x4")
        val matrix2x3 = Uniform.mat2x3.create("matrix2x3")
        val matrix2x2 = Uniform.mat2.create("matrix2x2")
    }
}