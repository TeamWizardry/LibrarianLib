@file:Suppress("BooleanLiteralArgument")

package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.mojang.blaze3d.matrix.MatrixStack
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object BoolArrayUniform: ShaderTest<BoolArrayUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

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
        fr.drawString(matrixStack, "$index",
            (maxX - 2 - fr.getStringWidth("$index")).toInt().toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("bool_array_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/bool_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glBool[2]
        val vector2 = GLSL.bvec2[2]
        val vector3 = GLSL.bvec3[2]
        val vector4 = GLSL.bvec4[2]
    }
}