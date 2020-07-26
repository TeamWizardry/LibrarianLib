package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.GLSLStruct
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

internal object StructUniform: ShaderTest<StructUniform.Test>() {

    override fun doDraw() {
        val minX = -32.0
        val minY = 0.0
        val maxX = 160.0
        val maxY = 128.0

        val c = Color.WHITE

        shader.simple.primitive.set(1f)
        shader.simple.primitiveArray[0] = 2f
        shader.simple.primitiveArray[1] = 3f
        shader.simple.embedded.embed.set(4f)
        shader.simple.embeddedArray[0].embed.set(5f)
        shader.simple.embeddedArray[1].embed.set(6f)

        shader.simpleArray[0].primitive.set(11f)
        shader.simpleArray[0].primitiveArray[0] = 12f
        shader.simpleArray[0].primitiveArray[1] = 13f
        shader.simpleArray[0].embedded.embed.set(14f)
        shader.simpleArray[0].embeddedArray[0].embed.set(15f)
        shader.simpleArray[0].embeddedArray[1].embed.set(16f)

        shader.simpleArray[1].primitive.set(21f)
        shader.simpleArray[1].primitiveArray[0] = 22f
        shader.simpleArray[1].primitiveArray[1] = 23f
        shader.simpleArray[1].embedded.embed.set(24f)
        shader.simpleArray[1].embeddedArray[0].embed.set(25f)
        shader.simpleArray[1].embeddedArray[1].embed.set(26f)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).tex(0f, 1f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 1f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        shader.bind()
        buffer.finish()
        shader.unbind()
    }

    private val renderType = SimpleRenderTypes.flat(ResourceLocation("minecraft:missingno"), GL11.GL_QUADS)

    class Test: Shader("struct_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/struct_tests.frag")) {
        val simple = GLSL.struct<Simple>()
        val simpleArray = GLSL.struct<Simple>(2)

        class Embedded: GLSLStruct() {
            val embed = GLSL.glFloat()
        }
        class Simple: GLSLStruct() {
            val primitive = GLSL.glFloat()
            val primitiveArray = GLSL.glFloat[2]
            val embedded = GLSL.struct<Embedded>()
            val embeddedArray = GLSL.struct<Embedded>(2)
        }
    }
}