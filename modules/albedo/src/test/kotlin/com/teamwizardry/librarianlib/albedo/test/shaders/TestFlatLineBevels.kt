package com.teamwizardry.librarianlib.albedo.test.shaders

import com.teamwizardry.librarianlib.albedo.base.buffer.FlatColorRenderBuffer
import com.teamwizardry.librarianlib.albedo.base.buffer.FlatLinesRenderBuffer
import com.teamwizardry.librarianlib.albedo.buffer.Primitive
import com.teamwizardry.librarianlib.albedo.test.ShaderTest
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.vec
import com.teamwizardry.librarianlib.math.*
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal object TestFlatLineBevels : ShaderTest(150, 150) {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        val center = vec(width / 2, height / 2, 0)
        val direction = (vec(mousePos.x, mousePos.y, 0) - center).normalize()
        val inset = 15f
        val outset = 25f
        val length = width / 2

        val startDelta = vec(-length, 0, 0)
        val startNormal = vec(0, 1, 0) / Client.scaleFactor
        val endDelta = direction * length
        val endNormal = vec(-direction.y, direction.x, 0) / Client.scaleFactor

        run {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center).color(1f, 1f, 1f, 1f).endVertex()

            val radius = length + 20
            val steps = 50
            val stepSize = 2 * PI / steps
            for (i in 0..steps) {
                val angle = i * stepSize
                rb.pos(stack, center + vec(sin(angle), cos(angle), 0) * radius).color(1f, 1f, 1f, 1f).endVertex()
            }

            rb.draw(Primitive.TRIANGLE_FAN)
        }

        run {
            val rb = FlatLinesRenderBuffer.SHARED

            var color = Color(0xc65411)
            rb.pos(stack, center + startDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)

            color = Color(0x3A9109)
            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center + endDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)
        }

        run {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center + startDelta).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center + startDelta + startNormal * outset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - startDelta + startNormal * outset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + startDelta - startNormal * inset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - startDelta - startNormal * inset).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center + endDelta + endNormal * outset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - endDelta + endNormal * outset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta - endNormal * inset).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - endDelta - endNormal * inset).color(0f, 0f, 0f, 1f).endVertex()

            rb.draw(Primitive.LINES)
        }
    }
}