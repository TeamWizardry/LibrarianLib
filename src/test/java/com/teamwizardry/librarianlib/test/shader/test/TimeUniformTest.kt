package com.teamwizardry.librarianlib.test.shader.test

import com.teamwizardry.librarianlib.features.neogui.GuiBase
import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.toRl
import com.teamwizardry.librarianlib.features.shader.Shader
import com.teamwizardry.librarianlib.features.shader.ShaderHelper
import com.teamwizardry.librarianlib.features.shader.uniforms.UniformFloatTime
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

class TimeUniformTest: GuiBase() {

    init {
        main.size = vec(100, 100)
        main.add(object: GuiLayer(0, 0, 100, 100) {
            override fun draw(partialTicks: Float) {
                super.draw(partialTicks)
                ShaderHelper.useShader(TimeUniformShader)
                Minecraft.getMinecraft().renderEngine.bindTexture("minecraft:textures/blocks/dirt.png".toRl())

                val tessellator = Tessellator.getInstance()
                val vb = tessellator.buffer
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                vb.pos(0.0, 100.0, 0.0).tex(0.0, 1.0).endVertex()
                vb.pos(100.0, 100.0, 0.0).tex(1.0, 1.0).endVertex()
                vb.pos(100.0, 0.0, 0.0).tex(1.0, 0.0).endVertex()
                vb.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex()
                tessellator.draw()

                ShaderHelper.releaseShader()
            }
        })
    }

    object TimeUniformShader: Shader(null, "shaders/time_uniform.frag") {
        val time = UniformFloatTime()
        init { ShaderHelper.addShader(this) }
    }
}