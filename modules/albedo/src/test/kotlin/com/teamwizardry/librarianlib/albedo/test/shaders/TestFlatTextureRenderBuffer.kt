package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.systems.RenderSystem
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatTextureRenderBuffer
import com.teamwizardry.librarianlib.albedo.shader.StandardUniforms
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.buffer.RenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.VertexBuffer
import com.teamwizardry.librarianlib.albedo.shader.Shader
import com.teamwizardry.librarianlib.albedo.shader.attribute.VertexLayoutElement
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.albedo.shader.uniform.Uniform
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.math.Matrix4d
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

internal object TestFlatTextureRenderBuffer : ShaderTest() {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d) {
        RenderSystem.enableBlend()
        RenderSystem.disableTexture()
        RenderSystem.defaultBlendFunc()

        val rb = FlatTextureRenderBuffer.SHARED

        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).tex(0f, 0f).endVertex()
        rb.pos(matrix, minX, maxY, 0).color(1f, 1f, 0f, 1f).tex(0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).color(1f, 1f, 1f, 1f).tex(1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).color(0f, 1f, 0f, 1f).tex(1f, 0f).endVertex()

        rb.texture.set(Client.textureManager.getTexture(Identifier("minecraft:textures/block/grass_block_side.png")).glId)
        rb.draw(Primitive.QUADS)
        RenderSystem.enableTexture()
    }
}