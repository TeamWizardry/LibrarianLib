package com.teamwizardry.librarianlib.albedo.test.renderers

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.AlbedoTestRenderer
import net.minecraft.client.util.math.MatrixStack

object FlatColorTestRenderer : AlbedoTestRenderer() {
    override fun render(matrices: MatrixStack) {
        val vb = FlatColorRenderBuffer.SHARED

        vb.pos(matrices, 0, 0, 0).color(1f, 0f, 1f, 1f).endVertex()
        vb.pos(matrices, 0, 1, 0).color(1f, 0f, 1f, 1f).endVertex()
        vb.pos(matrices, 1, 1, 0).color(1f, 0f, 1f, 1f).endVertex()

        vb.pos(matrices, 1, 1, 0).color(1f, 0f, 1f, 1f).endVertex()
        vb.pos(matrices, 0, 1, 0).color(1f, 0f, 1f, 1f).endVertex()
        vb.pos(matrices, 0, 0, 0).color(1f, 0f, 1f, 1f).endVertex()

        vb.draw(Primitive.TRIANGLES)
    }
}