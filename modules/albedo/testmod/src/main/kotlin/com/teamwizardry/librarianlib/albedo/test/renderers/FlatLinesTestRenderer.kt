package com.teamwizardry.librarianlib.albedo.test.renderers

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatLinesRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.AlbedoTestRenderer
import com.teamwizardry.librarianlib.core.util.vec
import net.minecraft.client.util.math.MatrixStack

object FlatLinesTestRenderer : AlbedoTestRenderer() {
    override fun render(matrices: MatrixStack) {
        val vb = FlatLinesRenderBuffer.SHARED

        val points = listOf(
            vec(0, 0, 1),

            vec(0, 0, 0),
            vec(0, 1, 0),
            vec(0, 1, 1),
            vec(0, 0.1, 0.5),
            vec(0, 0, 1),
            vec(0, 0, 0),

            vec(0, 1, 0),
        )

        matrices.translate(0.0, 1.0, 0.0)
        points.forEach {
            vb.pos(matrices, it).color(1f, 0f, 1f, 1f).width(10f).endVertex()
        }
        vb.draw(Primitive.LINE_STRIP_ADJACENCY)

        points.forEach {
            vb.pos(matrices, it).color(0f, 0f, 0f, 1f).width(1f).endVertex()
        }
        vb.draw(Primitive.LINE_STRIP_ADJACENCY)
    }
}