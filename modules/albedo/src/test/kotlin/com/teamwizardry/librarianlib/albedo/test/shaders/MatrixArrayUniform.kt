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

internal object MatrixArrayUniform: ShaderTest<MatrixArrayUniform.Test>(
    minX = -8,
    minY = -8,
    maxX = 136,
    maxY = 136
) {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        val matrixType = (Client.time.seconds % 3).toInt()
        val index = (Client.time.seconds % 6).toInt()/3
        val mat4Label: String = when(matrixType) {
            0 -> "direct"
            1 -> "Matrix4d"
            else -> "Matrix4f"
        }
        val mat3Label: String = when(matrixType) {
            0 -> "direct"
            1 -> "Matrix3d"
            else -> "Matrix3f"
        }
        shader.index.set(index)

        (0..1).forEach { i ->
            val d = if (i == 0) 0 else 100
            when (matrixType) {
                0 -> {
                    shader.matrix4x4.set(i,
                        d + 00f, d + 10f, d + 20f, d + 30f, // column 0
                        d + 01f, d + 11f, d + 21f, d + 31f, // column 1
                        d + 02f, d + 12f, d + 22f, d + 32f, // column 2
                        d + 03f, d + 13f, d + 23f, d + 33f  // column 3
                    )
                }
                1 -> {
                    shader.matrix4x4.set(i, Matrix4d(
                        d + 00.0, d + 01.0, d + 02.0, d + 03.0,
                        d + 10.0, d + 11.0, d + 12.0, d + 13.0,
                        d + 20.0, d + 21.0, d + 22.0, d + 23.0,
                        d + 30.0, d + 31.0, d + 32.0, d + 33.0
                    ))
                }
                else -> {
                    shader.matrix4x4.set(i, Matrix4d(
                        d + 00f, d + 01f, d + 02f, d + 03f,
                        d + 10f, d + 11f, d + 12f, d + 13f,
                        d + 20f, d + 21f, d + 22f, d + 23f,
                        d + 30f, d + 31f, d + 32f, d + 33f
                    ).toMatrix4f())
                }
            }
            shader.matrix4x3.set(i,
                d+00f, d+10f, d+20f, // column 0
                d+01f, d+11f, d+21f, // column 1
                d+02f, d+12f, d+22f, // column 2
                d+03f, d+13f, d+23f  // column 3
            )
            shader.matrix4x2.set(i,
                d+00f, d+10f, // column 0
                d+01f, d+11f, // column 1
                d+02f, d+12f, // column 2
                d+03f, d+13f  // column 3
            )

            shader.matrix3x4.set(i,
                d+00f, d+10f, d+20f, d+30f, // column 0
                d+01f, d+11f, d+21f, d+31f, // column 1
                d+02f, d+12f, d+22f, d+32f  // column 2
            )

            when (matrixType) {
                0 -> {
                    shader.matrix3x3.set(i,
                        d+00f, d+10f, d+20f, // column 0
                        d+01f, d+11f, d+21f, // column 1
                        d+02f, d+12f, d+22f  // column 2
                    )
                }
                1 -> {
                    shader.matrix3x3.set(i, Matrix3d(
                        d+00.0, d+01.0, d+02.0,
                        d+10.0, d+11.0, d+12.0,
                        d+20.0, d+21.0, d+22.0
                    ))
                }
                else -> {
                    shader.matrix3x3.set(i, Matrix3d(
                        d+00.0, d+01.0, d+02.0,
                        d+10.0, d+11.0, d+12.0,
                        d+20.0, d+21.0, d+22.0
                    ).toMatrix3f())
                }
            }
            shader.matrix3x2.set(i,
                d+00f, d+10f, // column 0
                d+01f, d+11f, // column 1
                d+02f, d+12f  // column 2
            )

            shader.matrix2x4.set(i,
                d+00f, d+10f, d+20f, d+30f, // column 0
                d+01f, d+11f, d+21f, d+31f  // column 1
            )
            shader.matrix2x3.set(i,
                d+00f, d+10f, d+20f, // column 0
                d+01f, d+11f, d+21f  // column 1
            )
            shader.matrix2x2.set(i,
                d+00f, d+10f, // column 0
                d+01f, d+11f  // column 1
            )
        }

        drawUnitQuad(matrix)

        val fr = Client.minecraft.textRenderer
        val cellSize = 16
        fr.draw(
            stack, mat4Label,
            (minX + cellSize * 2 - fr.getWidth(mat4Label)/2).toFloat(),
            (minY + cellSize * 2 - 4).toFloat(),
            Color.WHITE.rgb
        )
        fr.draw(
            stack, mat3Label,
            (minX + cellSize * 5.5 - fr.getWidth(mat3Label)/2).toInt().toFloat(),
            (minY + cellSize * 5.5 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )
        fr.draw(
            stack, "$index",
            (maxX - 2 - fr.getWidth("$index")).toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    class Test: Shader("matrix_array_tests", Identifier("liblib-albedo-test:shaders/uniform_base.vert"), Identifier("liblib-albedo-test:shaders/matrix_array_tests.frag")) {
        val index = Uniform.int.create("index")

        val matrix4x4 = Uniform.mat4.createArray("matrix4x4", false, 2)
        val matrix4x3 = Uniform.mat4x3.createArray("matrix4x3", false, 2)
        val matrix4x2 = Uniform.mat4x2.createArray("matrix4x2", false, 2)

        val matrix3x4 = Uniform.mat3x4.createArray("matrix3x4", false, 2)
        val matrix3x3 = Uniform.mat3.createArray("matrix3x3", false, 2)
        val matrix3x2 = Uniform.mat3x2.createArray("matrix3x2", false, 2)

        val matrix2x4 = Uniform.mat2x4.createArray("matrix2x4", false, 2)
        val matrix2x3 = Uniform.mat2x3.createArray("matrix2x3", false, 2)
        val matrix2x2 = Uniform.mat2.createArray("matrix2x2", false, 2)
    }
}