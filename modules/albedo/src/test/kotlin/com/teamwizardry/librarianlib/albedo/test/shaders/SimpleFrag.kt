package com.teamwizardry.librarianlib.albedo.test.shaders

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

object SimpleFrag: ShaderTest<SimpleFrag.Test>() {

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 120.0
        val maxY = 120.0

        val c = Color.WHITE

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

    class Test: Shader("simple_frag", null, Identifier("liblib-albedo-test:shaders/simple_frag.frag")) {

    }
}

