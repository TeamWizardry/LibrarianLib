package com.teamwizardry.librarianlib.albedo.testmod.shaders

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

object SimpleFrag: ShaderTest<SimpleFrag.Test>() {

    override fun doDraw() {
        val minX = 0.0
        val minY = 0.0
        val maxX = 120.0
        val maxY = 120.0

        val c = Color.WHITE

        shader.bind()
        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).endVertex()
        vb.pos2d(maxX, maxY).color(c).endVertex()
        vb.pos2d(maxX, minY).color(c).endVertex()
        vb.pos2d(minX, minY).color(c).endVertex()

        buffer.finish()
        shader.unbind()
    }

    private val renderType = SimpleRenderTypes.flat(GL11.GL_QUADS)

    class Test: Shader("simple_frag", null, ResourceLocation("librarianlib-albedo-test:shaders/simple_frag.frag")) {

    }
}

