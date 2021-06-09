package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.Shader
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.awt.Color

object SimpleFrag: ShaderTest<SimpleFrag.Test>() {

    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        drawUnitQuad(matrix)
    }

    class Test: Shader("simple_frag", null, Identifier("liblib-albedo-test:shaders/simple_frag.frag")) {

    }
}

