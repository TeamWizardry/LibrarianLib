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
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import java.awt.Color
import kotlin.math.*

internal object TestFlatLineBevels : ShaderTest(220, 220) {
    override fun doDraw(stack: MatrixStack, matrix: Matrix4d, mousePos: Vec2d) {
        val center = vec(width / 2, height / 2, 0)
//        val direction = (vec(mousePos.x, mousePos.y, 0) - center).normalize()
        val directionAngle = Client.time.seconds * 2 * PI / 5
        val direction = vec(sin(directionAngle), cos(directionAngle), 0)
        val inset = -30f
        val outset = -5f
        val length = 65
        val radius = 110

        val startDelta = vec(-length, 0, 0)
        val startNormal = vec(0, 1, 0) / Client.scaleFactor
        val endDelta = direction * length
        val endNormal = vec(-direction.y, direction.x, 0) / Client.scaleFactor
        val tipDelta = vec(-length / 2, 0, 0)
        val tipNormal = vec(0, -1, 0) / Client.scaleFactor

        run {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center).color(0.2f, 0.2f, 0.2f, 1f).endVertex()

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

            var color = Color(0.78f, 0.33f, 0.07f, 1f)
            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)

            color = Color(0.23f, 0.57f, 0.04f, 1f)
            rb.pos(stack, center + startDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center + endDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta + tipDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)

            color = Color(0.05f, 0.51f, 0.67f, 1f)

            rb.pos(stack, center).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta).inset(inset).outset(outset).color(color).endVertex()
            rb.pos(stack, center + endDelta + tipDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.pos(stack, center + endDelta + tipDelta).inset(inset).outset(outset).color(color).endVertex()

            rb.draw(Primitive.LINE_STRIP_ADJACENCY)
        }

        if(!Screen.hasShiftDown())  {
            val rb = FlatColorRenderBuffer.SHARED

            rb.pos(stack, center + startDelta).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta).color(0f, 0f, 0f, 1f).endVertex()

            rb.pos(stack, center + endDelta).color(0f, 0f, 0f, 1f).endVertex()
            rb.pos(stack, center + endDelta + tipDelta).color(0f, 0f, 0f, 1f).endVertex()

            fun line(offset: Vec3d, color: Color) {
                val intercept = vec(offset.y, -offset.x, offset.z).normalize() * (sqrt(1 - offset.lengthSquared() / (radius * radius)) * radius)
                rb.pos(stack, center + offset + intercept).color(color).endVertex()
                rb.pos(stack, center + offset - intercept).color(color).endVertex()
            }

            val red = Color(1f, 0f, 0f, 0.5f)
            val green = Color(0f, 0.75f, 0f, 0.5f)
            line(startNormal * outset, red)
            line(startNormal * inset, red)

            line(endNormal * outset, red)
            line(endNormal * inset, red)

            line(vec(0, endDelta.y, 0) + tipNormal * outset, red)
            line(vec(0, endDelta.y, 0) + tipNormal * inset, red)

            val bevelCoefficient = 1.5
            run {
                val insetBevel = inset * bevelCoefficient / Client.scaleFactor
                val outsetBevel = outset * bevelCoefficient / Client.scaleFactor

                val cornerNormal = (vec(startDelta.y, -startDelta.x, startDelta.z).normalize() +
                        vec(-endDelta.y, endDelta.x, endDelta.z).normalize()).normalize()
                val cornerPerpendicular = vec(cornerNormal.y, -cornerNormal.x, cornerNormal.z)
                rb.pos(stack, center + cornerNormal * insetBevel).color(green).endVertex()
                rb.pos(stack, center + cornerNormal * outsetBevel).color(green).endVertex()

                rb.pos(stack, center + cornerNormal * insetBevel - cornerPerpendicular * inset).color(green)
                    .endVertex()
                rb.pos(stack, center + cornerNormal * insetBevel + cornerPerpendicular * inset).color(green)
                    .endVertex()

                rb.pos(stack, center + cornerNormal * outsetBevel - cornerPerpendicular * outset).color(green)
                    .endVertex()
                rb.pos(stack, center + cornerNormal * outsetBevel + cornerPerpendicular * outset).color(green)
                    .endVertex()
            }

            run {
                val insetBevel = inset * bevelCoefficient / Client.scaleFactor
                val outsetBevel = outset * bevelCoefficient / Client.scaleFactor
                val cornerNormal = (vec(-tipDelta.y, tipDelta.x, tipDelta.z).normalize() +
                        vec(-endDelta.y, endDelta.x, endDelta.z).normalize()).normalize()
                val cornerPerpendicular = vec(cornerNormal.y, -cornerNormal.x, cornerNormal.z)
                rb.pos(stack, center + endDelta + cornerNormal * insetBevel).color(green).endVertex()
                rb.pos(stack, center + endDelta + cornerNormal * outsetBevel).color(green).endVertex()

                rb.pos(stack, center + endDelta + cornerNormal * insetBevel - cornerPerpendicular * inset).color(green)
                    .endVertex()
                rb.pos(stack, center + endDelta + cornerNormal * insetBevel + cornerPerpendicular * inset).color(green)
                    .endVertex()

                rb.pos(stack, center + endDelta + cornerNormal * outsetBevel - cornerPerpendicular * outset).color(green)
                    .endVertex()
                rb.pos(stack, center + endDelta + cornerNormal * outsetBevel + cornerPerpendicular * outset).color(green)
                    .endVertex()
            }

            rb.draw(Primitive.LINES)
        }
    }
}