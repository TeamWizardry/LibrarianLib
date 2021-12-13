package com.teamwizardry.librarianlib.albedo.test.renderers

import com.teamwizardry.librarianlib.albedo.base.buffer.ShadedTextureRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.AlbedoTestRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

object ShadedTextureTestRenderer : AlbedoTestRenderer() {
    override fun render(matrices: MatrixStack) {
        val rb = ShadedTextureRenderBuffer.SHARED
        rb.texture.set(Identifier("minecraft:textures/block/grass_block_side.png"))
        rb.pos(matrices, 0, 2, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()
        rb.pos(matrices, 0, 2, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()
        rb.pos(matrices, 0, 0, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()
        rb.pos(matrices, 0, 0, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()
        rb.pos(matrices, 0, 2, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()
        rb.pos(matrices, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, 1, 0)
            .endVertex()

        rb.pos(matrices, 0, 0, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()
        rb.pos(matrices, 0, 2, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()
        rb.pos(matrices, 0, 0, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()
        rb.pos(matrices, 0, 0, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()
        rb.pos(matrices, 0, 2, 0).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()
        rb.pos(matrices, 0, 2, 1).color(1f, 1f, 1f, 1f).tex(0.0, 0.0).light(15, 15).normal(matrices, 0, -1, 0)
            .endVertex()

        rb.draw(Primitive.TRIANGLES)
    }
}