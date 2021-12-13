package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.util.math.MatrixStack

internal object TestFlatColorRenderBuffer : ShaderTest() {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        val rb = FlatColorRenderBuffer.SHARED
        rb.pos(matrix, minX, minY, 0).color(1f, 0f, 0f, 1f).endVertex()
        rb.pos(matrix, minX, maxY, 0).color(1f, 1f, 0f, 1f).endVertex()
        rb.pos(matrix, maxX, maxY, 0).color(1f, 1f, 1f, 1f).endVertex()
        rb.pos(matrix, maxX, minY, 0).color(0f, 1f, 0f, 1f).endVertex()

        rb.draw(Primitive.QUADS)
    }
}