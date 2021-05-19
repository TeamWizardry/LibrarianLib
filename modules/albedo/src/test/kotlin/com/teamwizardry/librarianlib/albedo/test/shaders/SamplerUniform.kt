package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderLayerPhaseParameters
import com.teamwizardry.librarianlib.core.rendering.DefaultRenderPhases
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.core.util.mixinCast
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

internal object SamplerUniform: ShaderTest<SamplerUniform.Test>() {
    private val failureLocation = Identifier("liblib-albedo-test:textures/sampler_failure.png")
    private val successLocation1 = Identifier("liblib-albedo-test:textures/sampler_success1.png")
    private val successLocation2 = Identifier("liblib-albedo-test:textures/sampler_success2.png")

    override fun doDraw(matrixStack: MatrixStack) {
        val minX = 0.0
        val minY = 0.0
        val maxX = 128.0
        val maxY = 128.0

        val c = Color.WHITE

        Client.textureManager.bindTexture(successLocation1)
        Client.textureManager.bindTexture(successLocation2)
        val tex1 = Client.textureManager.getTexture(successLocation1)?.glId ?: throw IllegalStateException("sampler_success1 not found")
        val tex2 = Client.textureManager.getTexture(successLocation2)?.glId ?: throw IllegalStateException("sampler_success2 not found")
        Client.textureManager.bindTexture(failureLocation)

        val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
        var vb = buffer.getBuffer(renderType)
        vb.vertex2d(minX, maxY).color(c).texture(0f, 1f).next()
        vb.vertex2d(maxX, maxY).color(c).texture(1f, 1f).next()
        vb.vertex2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.vertex2d(minX, minY).color(c).texture(0f, 0f).next()

        buffer.draw()

        shader.sampler1.set(tex1)
        shader.sampler2.set(tex2)

        vb = buffer.getBuffer(renderType)

        vb.vertex2d(minX, maxY).color(c).texture(0f, 2f).next()
        vb.vertex2d(maxX, maxY).color(c).texture(1f, 2f).next()
        vb.vertex2d(maxX, minY).color(c).texture(1f, 0f).next()
        vb.vertex2d(minX, minY).color(c).texture(0f, 0f).next()

        buffer.draw()
    }

    private val renderType: RenderLayer
    init {

        val renderState = RenderLayer.MultiPhaseParameters.builder()
            .alpha(DefaultRenderPhases.ONE_TENTH_ALPHA)
            .depthTest(DefaultRenderPhases.LEQUAL_DEPTH_TEST)
            .transparency(DefaultRenderPhases.TRANSLUCENT_TRANSPARENCY)
            .build(true)

        mixinCast<IMutableRenderLayerPhaseParameters>(renderState).addPhase("albedo", { shader.bind() }, { shader.unbind() })

        renderType = SimpleRenderLayers.makeType("sampler",
            VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, false, false, renderState
        )
    }

    class Test: Shader("sampler_tests", null, Identifier("liblib-albedo-test:shaders/sampler_tests.frag")) {
        // we only test sampler2D because all the sampler implementations are identical, and the others will be complex
        // to set up
        val sampler1 = GLSL.sampler2D()
        val sampler2 = GLSL.sampler2D()
    }
}