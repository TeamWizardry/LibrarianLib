package com.teamwizardry.librarianlib.albedo.test.shaders

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
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
        val inset = -15f
        val outset = 30f
        val length = width / 2

        val startDelta = vec(-length, 0, 0)
        val startNormal = vec(0, 1, 0) / Client.scaleFactor
        val endDelta = direction * length
        val endNormal = vec(-direction.y, direction.x, 0) / Client.scaleFactor

        run {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center).color(0.2f, 0.2f, 0.2f, 1f).endVertex()

            val radius = length + 20
            val steps = 50
            val stepSize = 2 * PI / steps
            for (i in 0..steps) {
                val angle = i * stepSize
                rb.pos(stack, center + vec(sin(angle), cos(angle), 0) * radius).color(0.2f, 0.2f, 0.2f, 1f).endVertex()
            }

            rb.draw(Primitive.TRIANGLE_FAN)
        }

        run {
            val rb = FlatLinesRenderBuffer.SHARED

            var color = Color(0.78f, 0.33f, 0.07f, 0.5f)
//            rb.pos(stack, center + startDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()
//
//            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()
//            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()
//
//            rb.pos(stack, center + endDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()
//
//            rb.draw(Primitive.LINE_STRIP_ADJACENCY)

            RenderSystem.disableCull()
            color = Color(0.23f, 0.57f, 0.04f, 0.5f)
            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center + endDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta * 1.1).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)
            RenderSystem.enableCull()
        }

        run {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center + startDelta).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta).color(0f, 0f, 0f, 1f).endVertex()

            val br = 1f
            val bg = 0.75f
            rb.pos(stack, center + startDelta + startNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - startDelta + startNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + startDelta + startNormal * inset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - startDelta + startNormal * inset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + startDelta + startNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + startDelta + startNormal * inset).color(br, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center + endDelta + endNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - endDelta + endNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta + endNormal * inset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center - endDelta + endNormal * inset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta + endNormal * outset).color(br, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta + endNormal * inset).color(br, 0f, 0f, 1f).endVertex()

            val insetBevel = inset * 1.5 / Client.scaleFactor
            val outsetBevel = outset * 1.5 / Client.scaleFactor
            val cornerNormal = (vec(startDelta.y, -startDelta.x, startDelta.z).normalize() + vec(-endDelta.y, endDelta.x, endDelta.z).normalize()).normalize()
            val cornerPerpendicular = vec(cornerNormal.y, -cornerNormal.x, cornerNormal.z)
            rb.pos(stack, center + cornerNormal * insetBevel).color(0f, bg, 0f, 1f).endVertex()
            rb.pos(stack, center + cornerNormal * (outsetBevel + 15)).color(0f, bg, 0f, 1f).endVertex()

            rb.pos(stack, center + cornerNormal * insetBevel - cornerPerpendicular * 50.0).color(0f, bg, 0f, 1f).endVertex()
            rb.pos(stack, center + cornerNormal * insetBevel + cornerPerpendicular * 50.0).color(0f, bg, 0f, 1f).endVertex()

            rb.pos(stack, center + cornerNormal * outsetBevel - cornerPerpendicular * 50.0).color(0f, bg, 0f, 1f).endVertex()
            rb.pos(stack, center + cornerNormal * outsetBevel + cornerPerpendicular * 50.0).color(0f, bg, 0f, 1f).endVertex()

            rb.draw(Primitive.LINES)
        }
    }
}