package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.uniform.Uniform
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object SamplerArrayUniform: ShaderTest<SamplerArrayUniform.Test>() {
    private val failureLocation = Identifier("liblib-albedo-test:textures/sampler_failure.png")
    private val successLocation1 = Identifier("liblib-albedo-test:textures/sampler_success1.png")
    private val successLocation2 = Identifier("liblib-albedo-test:textures/sampler_success2.png")

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        Client.textureManager.bindTexture(successLocation1)
        Client.textureManager.bindTexture(successLocation2)
        val tex1 = Client.textureManager.getTexture(successLocation1)?.glId ?: throw IllegalStateException("sampler_success1 not found")
        val tex2 = Client.textureManager.getTexture(successLocation2)?.glId ?: throw IllegalStateException("sampler_success2 not found")
        Client.textureManager.bindTexture(failureLocation)

        drawUnitQuad(matrix)

        val index = (Client.time.seconds % 2).toInt()
        shader.index.set(index)
        shader.sampler1[0] = tex2
        shader.sampler1[1] = tex1
        shader.sampler2[0] = tex1
        shader.sampler2[1] = tex2

        drawUnitQuad(matrix, maxV = 2f)
    }

    class Test: Shader("sampler_array_tests", null, Identifier("liblib-albedo-test:shaders/sampler_array_tests.frag")) {
        val index = Uniform.int.create()
        // we only test sampler2D because all the sampler implementations are identical, and the others will be complex
        // to set up
        val sampler1 = Uniform.sampler2D.createArray(2)
        val sampler2 = Uniform.sampler2D.createArray(2)
    }
}