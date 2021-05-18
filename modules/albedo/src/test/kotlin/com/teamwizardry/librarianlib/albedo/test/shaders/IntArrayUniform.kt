package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object IntArrayUniform: ShaderTest<IntArrayUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

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

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.vertex2d(minX, maxY).color(c).texture(0f, 1f).next()
        vb.vertex2d(maxX, maxY).color(c).texture(1f, 1f).next()
        vb.vertex2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.vertex2d(minX, minY).color(c).texture(0f, 0f).next()

        shader.bind()
        buffer.draw()
        shader.unbind()

        val fr = Client.minecraft.textRenderer
        fr.draw(matrixStack, "$index",
            (maxX - 2 - fr.getWidth("$index")).toInt().toFloat(),
            minY.toFloat() + 11,
            Color.WHITE.rgb
        )
    }

    private val renderType = SimpleRenderLayers.flat(Identifier("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("int_array_tests", null, Identifier("liblib-albedo-test:shaders/int_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glInt[2]
        val vector2 = GLSL.ivec2[2]
        val vector3 = GLSL.ivec3[2]
        val vector4 = GLSL.ivec4[2]
    }
}