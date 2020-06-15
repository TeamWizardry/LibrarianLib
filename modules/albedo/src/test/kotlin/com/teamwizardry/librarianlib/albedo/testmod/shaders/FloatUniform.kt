package com.teamwizardry.librarianlib.albedo.testmod.shaders

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

object FloatUniform: ShaderTest() {

    override fun doDraw() {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        TestUniform.bind()
        TestUniform.primitive.set(10f)
        TestUniform.vector2.set(10f, 20f)
        TestUniform.vector3.set(10f, 20f, 30f)
        TestUniform.vector4.set(10f, 20f, 30f, 40f)
        TestUniform.pushUniforms()

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).tex(0f, 4f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 4f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        buffer.finish()
        TestUniform.unbind()
    }

    override val shader: Shader
        get() = TestUniform

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    private object TestUniform: Shader("float_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/float_tests.frag")) {
        val primitive = GLSL.glFloat()
        val vector2 = GLSL.vec2()
        val vector3 = GLSL.vec3()
        val vector4 = GLSL.vec4()
    }
}