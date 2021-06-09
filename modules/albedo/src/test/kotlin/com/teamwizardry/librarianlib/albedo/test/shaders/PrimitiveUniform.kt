package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.GLSL
import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import java.awt.Color

object PrimitiveUniform: ShaderTest<PrimitiveUniform.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        shader.time.set(Client.time.seconds)

        drawUnitQuad(matrix)
    }

    class Test: Shader("primitive_uniform", null, Identifier("liblib-albedo-test:shaders/primitive_uniform.frag")) {
        val time = GLSL.glFloat()
    }
}

