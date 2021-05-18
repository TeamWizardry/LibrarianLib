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

internal object BoolUniform: ShaderTest<BoolUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        shader.primitive.set(true)
        shader.vector2.set(true, false)
        shader.vector3.set(true, false, true)
        shader.vector4.set(true, false, true, false)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.vertex2d(minX, maxY).color(c).texture(0f, 1f).next()
        vb.vertex2d(maxX, maxY).color(c).texture(1f, 1f).next()
        vb.vertex2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.vertex2d(minX, minY).color(c).texture(0f, 0f).next()

        shader.bind()
        buffer.draw()
        shader.unbind()
    }

    private val renderType = SimpleRenderLayers.flat(Identifier("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("bool_tests", null, Identifier("liblib-albedo-test:shaders/bool_tests.frag")) {
        val primitive = GLSL.glBool()
        val vector2 = GLSL.bvec2()
        val vector3 = GLSL.bvec3()
        val vector4 = GLSL.bvec4()
    }
}