package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.math.Matrix3d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object MatrixArrayUniform: ShaderTest<MatrixArrayUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = -8.0
        val minY = -8.0
        val maxX = 136.0
        val maxY = 136.0

        val c = Color.WHITE



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
                    shader.matrix4x4.set(i, Matrix4f(floatArrayOf(
                        d + 00f, d + 01f, d + 02f, d + 03f,
                        d + 10f, d + 11f, d + 12f, d + 13f,
                        d + 20f, d + 21f, d + 22f, d + 23f,
                        d + 30f, d + 31f, d + 32f, d + 33f
                    )))
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

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).tex(0f, 1f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 1f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        shader.bind()
        buffer.finish()
        shader.unbind()

        val fr = Client.minecraft.fontRenderer
        val cellSize = 16
        fr.drawString(matrixStack, mat4Label,
            (minX + cellSize * 2 - fr.getStringWidth(mat4Label)/2).toInt().toFloat(),
            (minY + cellSize * 2 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )
        fr.drawString(matrixStack, mat3Label,
            (minX + cellSize * 5.5 - fr.getStringWidth(mat3Label)/2).toInt().toFloat(),
            (minY + cellSize * 5.5 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )
        fr.drawString(matrixStack, "$index",
            (maxX - 2 - fr.getStringWidth("$index")).toInt().toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("matrix_array_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/matrix_array_tests.frag")) {
        val index = GLSL.glInt()

        val matrix4x4 = GLSL.mat4[2]
        val matrix4x3 = GLSL.mat4x3[2]
        val matrix4x2 = GLSL.mat4x2[2]

        val matrix3x4 = GLSL.mat3x4[2]
        val matrix3x3 = GLSL.mat3[2]
        val matrix3x2 = GLSL.mat3x2[2]

        val matrix2x4 = GLSL.mat2x4[2]
        val matrix2x3 = GLSL.mat2x3[2]
        val matrix2x2 = GLSL.mat2[2]
    }
}