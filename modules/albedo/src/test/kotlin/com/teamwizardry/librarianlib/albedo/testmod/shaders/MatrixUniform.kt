package com.teamwizardry.librarianlib.albedo.testmod.shaders

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
import net.minecraft.client.renderer.Matrix4f
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object MatrixUniform: ShaderTest<MatrixUniform.Test>() {

    override fun doDraw() {
        val minX = -8.0
        val minY = -8.0
        val maxX = 136.0
        val maxY = 136.0

        val c = Color.WHITE



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
                shader.matrix4x4.set(Matrix4f(floatArrayOf(
                    00f, 01f, 02f, 03f,
                    10f, 11f, 12f, 13f,
                    20f, 21f, 22f, 23f,
                    30f, 31f, 32f, 33f
                )))
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
        fr.drawString(mat4Label,
            (minX + cellSize * 2 - fr.getStringWidth(mat4Label)/2).toInt().toFloat(),
            (minY + cellSize * 2 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )
        fr.drawString(mat3Label,
            (minX + cellSize * 5.5 - fr.getStringWidth(mat3Label)/2).toInt().toFloat(),
            (minY + cellSize * 5.5 - 4).toInt().toFloat(),
            Color.WHITE.rgb
        )

    }

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("matrix_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/matrix_tests.frag")) {
        val matrix4x4 = GLSL.mat4()
        val matrix4x3 = GLSL.mat4x3()
        val matrix4x2 = GLSL.mat4x2()

        val matrix3x4 = GLSL.mat3x4()
        val matrix3x3 = GLSL.mat3()
        val matrix3x2 = GLSL.mat3x2()

        val matrix2x4 = GLSL.mat2x4()
        val matrix2x3 = GLSL.mat2x3()
        val matrix2x2 = GLSL.mat2()
    }
}