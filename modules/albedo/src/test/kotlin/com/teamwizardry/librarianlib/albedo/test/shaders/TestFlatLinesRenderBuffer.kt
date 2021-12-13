package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatLinesRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.math.Matrix4d
import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.util.math.MatrixStack

internal object TestFlatLinesRenderBuffer : ShaderTest() {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        run {
            val rb = FlatLinesRenderBuffer.SHARED
            rb.pos(matrix, maxX, minY, 0).inset(-5f).outset(15f).color(0f, 1f, 0f, 1f).endVertex()

            rb.pos(matrix, minX, minY, 0).inset(-5f).outset(15f).color(1f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, minX, maxY, 0).inset(-5f).outset(15f).color(1f, 1f, 0f, 1f).endVertex()
            rb.pos(matrix, maxX, maxY, 0).inset(-5f).outset(15f).color(1f, 1f, 1f, 1f).endVertex()
            rb.pos(matrix, maxX, minY, 0).inset(-5f).outset(15f).color(0f, 1f, 0f, 1f).endVertex()
            rb.pos(matrix, minX, minY, 0).inset(-5f).outset(15f).color(1f, 0f, 0f, 1f).endVertex()

            rb.pos(matrix, minX, maxY, 0).inset(-5f).outset(15f).color(1f, 1f, 0f, 1f).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)
        }

        run {
            val rb = FlatColorRenderBuffer.SHARED
            rb.pos(matrix, minX, minY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, minX, maxY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, maxX, maxY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, maxX, minY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, minX, minY, 0).color(0f, 0f, 0f, 1f).endVertex()

            rb.draw(Primitive.LINE_STRIP)
        }

        val centerX = (minX + maxX) / 2
        val width = maxX - minX
        val height = maxY - minY

        val base = width / 2
        val triMinX = centerX - base / 2
        val triMinY = minY + height / 4
        val triMaxX = centerX + base / 2
        val triMaxY = maxY - 10

        run {
            val rb = FlatLinesRenderBuffer.SHARED
            rb.pos(matrix, centerX, triMinY, 0).width(25f).color(1f, 0f, 0f, 1f).endVertex()

            rb.pos(matrix, triMinX, triMaxY, 0).width(25f).color(1f, 1f, 0f, 1f).endVertex()
            rb.pos(matrix, triMaxX, triMaxY, 0).width(25f).color(0f, 1f, 0f, 1f).endVertex()
            rb.pos(matrix, centerX, triMinY, 0).width(25f).color(1f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, triMinX, triMaxY, 0).width(25f).color(1f, 1f, 0f, 1f).endVertex()

            rb.pos(matrix, triMaxX, triMaxY, 0).width(25f).color(0f, 1f, 0f, 1f).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)
        }

        run {
            val rb = FlatColorRenderBuffer.SHARED
            rb.pos(matrix, centerX, triMinY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, triMinX, triMaxY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, triMaxX, triMaxY, 0).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(matrix, centerX, triMinY, 0).color(0f, 0f, 0f, 1f).endVertex()

            rb.draw(Primitive.LINE_STRIP)
        }
    }
}