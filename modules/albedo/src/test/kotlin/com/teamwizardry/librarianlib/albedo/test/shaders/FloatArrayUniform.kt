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

internal object FloatArrayUniform: ShaderTest<FloatArrayUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

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

    class Test: Shader("float_array_tests", null, Identifier("liblib-albedo-test:shaders/float_array_tests.frag")) {
        val index = GLSL.glInt()
        val primitive = GLSL.glFloat[2]
        val vector2 = GLSL.vec2[2]
        val vector3 = GLSL.vec3[2]
        val vector4 = GLSL.vec4[2]
    }
}