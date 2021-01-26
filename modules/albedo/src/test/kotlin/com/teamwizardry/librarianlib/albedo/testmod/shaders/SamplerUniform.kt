package com.teamwizardry.librarianlib.albedo.testmod.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.testmod.ShaderTest
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.DefaultRenderStates
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.core.util.mixinCast
import dev.thecodewarrior.mirror.Mirror
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderState
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import java.awt.Color

internal object SamplerUniform: ShaderTest<SamplerUniform.Test>() {
    private val failureLocation = ResourceLocation("librarianlib-albedo-test:textures/sampler_failure.png")
    private val successLocation1 = ResourceLocation("librarianlib-albedo-test:textures/sampler_success1.png")
    private val successLocation2 = ResourceLocation("librarianlib-albedo-test:textures/sampler_success2.png")

    override fun doDraw() {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        Client.textureManager.bindTexture(successLocation1)
        Client.textureManager.bindTexture(successLocation2)
        val tex1 = Client.textureManager.getTexture(successLocation1)?.glTextureId ?: throw IllegalStateException("sampler_success1 not found")
        val tex2 = Client.textureManager.getTexture(successLocation2)?.glTextureId ?: throw IllegalStateException("sampler_success2 not found")
        Client.textureManager.bindTexture(failureLocation)

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        var vb = buffer.getBuffer(renderType)
        vb.pos2d(minX, maxY).color(c).tex(0f, 1f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 1f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        buffer.finish()

        shader.sampler1.set(tex1)
        shader.sampler2.set(tex2)

        vb = buffer.getBuffer(renderType)

        vb.pos2d(minX, maxY).color(c).tex(0f, 2f).endVertex()
        vb.pos2d(maxX, maxY).color(c).tex(1f, 2f).endVertex()
        vb.pos2d(maxX, minY).color(c).tex(1f, 0f).endVertex()
        vb.pos2d(minX, minY).color(c).tex(0f, 0f).endVertex()

        buffer.finish()
    }

    private val renderType: RenderType
    init {

        val renderState = RenderType.State.getBuilder()
            .alpha(DefaultRenderStates.DEFAULT_ALPHA)
            .depthTest(DefaultRenderStates.DEPTH_LEQUAL)
            .transparency(DefaultRenderStates.TRANSLUCENT_TRANSPARENCY)
            .build(true)

        mixinCast<IMutableRenderTypeState>(renderState).addState("albedo", { shader.bind() }, { shader.unbind() })

        renderType = SimpleRenderTypes.makeType("flat_texture",
            DefaultVertexFormats.POSITION_COLOR_TEX, GL11.GL_QUADS, 256, false, false, renderState
        )
    }

    class Test: Shader("sampler_tests", null, ResourceLocation("librarianlib-albedo-test:shaders/sampler_tests.frag")) {
        // we only test sampler2D because all the sampler implementations are identical, and the others will be complex
        // to set up
        val sampler1 = GLSL.sampler2D()
        val sampler2 = GLSL.sampler2D()
    }
}