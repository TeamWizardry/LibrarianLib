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

object PrimitiveUniform: ShaderTest<PrimitiveUniform.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 120.0
        val maxY = 120.0

        val c = Color.WHITE

        shader.time.set(Client.time.seconds)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.vertex2d(minX, maxY).color(c).next()
        vb.vertex2d(maxX, maxY).color(c).next()
        vb.vertex2d(maxX, minY).color(c).next()
        vb.vertex2d(minX, minY).color(c).next()

        shader.bind()
        buffer.draw()
        shader.unbind()
    }

    private val renderType = SimpleRenderLayers.flat(GL11.GL_QUADS)

    class Test: Shader("primitive_uniform", null, Identifier("liblib-albedo-test:shaders/primitive_uniform.frag")) {
        val time = GLSL.glFloat()
    }
}

