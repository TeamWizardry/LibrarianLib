package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.Minecraft
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by Demoniaque
 *
 * Will give you a full circular colorPrimary wheel WITHOUT a value bar. Implement that by yourself.
 */
class ComponentColorPicker(x: Int, y: Int, width: Int, height: Int) : GuiComponent(x, y, width, height) {

    private val colorWheelRadius = (width * 0.9 / 2.0) - 2.0 - 5.0
    private val valueMinRadius = colorWheelRadius + 1
    private val valueMainRadius = colorWheelRadius + 3
    private val valueMaxRadius = colorWheelRadius + 5

    private var colorWheelCursor = vec(colorWheelRadius, colorWheelRadius)
    private var colorWheelMouseDown = false
    private val half = vec(colorWheelRadius, colorWheelRadius)
    private var valueMouseDown = false
    private var valueCursor = vec(-valueMaxRadius, 0.0)
    private var value = 1.0

    var color = Color.WHITE

    class ColorChangeEvent(val color: Color) : Event()

    init {
        BUS.hook<GuiComponentEvents.MouseClickEvent> {
            if (mouseOver && !valueMouseDown && !colorWheelMouseDown) {
                val dist = mousePos.sub(half).length()
                if (dist >= valueMinRadius) {
                    selectValue(mousePos)
                } else selectColor(mousePos)
            }
        }

        BUS.hook<GuiComponentEvents.MouseDownEvent> {
            if (mouseOver) {
                val dist = mousePos.sub(half).length()
                if (dist >= valueMinRadius) {
                    valueMouseDown = true
                } else colorWheelMouseDown = true
            }
        }

        BUS.hook<GuiComponentEvents.MouseDragEvent> {
            if (colorWheelMouseDown) {
                selectColor(mousePos)
            } else if (valueMouseDown) {
                selectValue(mousePos)
            }
        }

        BUS.hook<GuiComponentEvents.MouseUpEvent> {
            colorWheelMouseDown = false
            valueMouseDown = false
        }
    }

    fun selectValue(mousePos: Vec2d) {
        val sub = mousePos.sub(vec(valueMaxRadius, valueMaxRadius))
        val rads = MathHelper.clamp(-Math.atan2(sub.y, sub.x), 0.0, Math.PI)

        valueCursor = vec(-MathHelper.sin(rads.toFloat() - Math.PI.toFloat() / 2.0f) * valueMaxRadius + valueMaxRadius, -MathHelper.cos(rads.toFloat() - Math.PI.toFloat() / 2.0f) * valueMaxRadius + valueMaxRadius).sub(4.0, 4.0)


        val degree = ((Math.toDegrees(rads) + 180.0) % 180.0)
        value = degree / 180.0
        //   Minecraft().player.sendChatMessage("${-Math.atan2(sub.y, sub.x)}")

        val hsv = Color.RGBtoHSB(color.red, color.green, color.blue, null)

        Minecraft().player.sendChatMessage("${color.red}, ${color.green}, ${color.blue}, -- ${hsv[0]}, ${hsv[1]}, ${hsv[2]}")
        color = Color.getHSBColor(hsv[0], hsv[1], value.toFloat())
        BUS.fire(ColorChangeEvent(color))
    }

    fun selectColor(mousePos: Vec2d) {
        val sub = mousePos.sub(half)
        val dist = sub.length()
        val rads = Math.atan2(sub.y, sub.x)
        val radius = colorWheelRadius - 2
        colorWheelCursor = if (dist < radius) {
            mousePos
        } else {
            vec(MathHelper.cos(rads.toFloat()) * radius + radius, MathHelper.sin(rads.toFloat()) * radius + radius).add(2.0, 2.0)
        }

        val degree = 90 - ((Math.toDegrees(rads) + 360.0) % 360.0)
        val hue = degree / 360.0
        val centerDist = colorWheelCursor.sub(half).length()

        Minecraft().player.sendChatMessage("degree = $degree, hue = $hue")

        val hsv = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsv)

        color = Color.getHSBColor(hue.toFloat(), centerDist.toFloat() / radius.toFloat(), value.toFloat())
        BUS.fire(ColorChangeEvent(color))
    }

    override fun draw(partialTicks: Float) {
        GlStateManager.pushMatrix()
        GlStateManager.enableBlend()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()

        val tess = Tessellator.getInstance()
        val bb = tess.buffer

        val vertexCount = 100.0
        val centerX = colorWheelRadius
        val centerY = colorWheelRadius

        var x: Double
        var y: Double
        var color = Color.DARK_GRAY

        // OUTER CIRCLE
        bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
        bb.pos(centerX, centerY, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        for (i in 0 until vertexCount.toInt()) {
            val angle = i * 2.0 * Math.PI / vertexCount

            x = centerX + MathHelper.cos(angle.toFloat()) * colorWheelRadius
            y = centerY + MathHelper.sin(angle.toFloat()) * colorWheelRadius

            bb.pos(y, x, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        }
        x = centerX + MathHelper.cos((2.0 * Math.PI).toFloat()) * colorWheelRadius
        y = centerY + MathHelper.sin((2.0 * Math.PI).toFloat()) * colorWheelRadius
        bb.pos(y, x, 0.0).color(color.red, color.green, color.blue, 255).endVertex()

        tess.draw()
        // OUTER CIRCLE


        // COLOR WHEEL
        var radius = colorWheelRadius - 2

        bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR)
        bb.pos(centerX, centerY, 0.0).color(1f, 1f, 1f, 1f).endVertex()

        for (i in 0 until vertexCount.toInt()) {
            val angle = i * 2.0 * Math.PI / vertexCount

            x = centerX + MathHelper.cos(angle.toFloat()) * radius
            y = centerY + MathHelper.sin(angle.toFloat()) * radius

            color = Color.getHSBColor((i / vertexCount).toFloat(), 1f, value.toFloat())
            bb.pos(y, x, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        }
        x = centerX + MathHelper.cos((2.0 * Math.PI).toFloat()) * radius
        y = centerY + MathHelper.sin((2.0 * Math.PI).toFloat()) * radius
        color = Color.getHSBColor(1f, 1f, value.toFloat())
        bb.pos(y, x, 0.0).color(color.red, color.green, color.blue, 255).endVertex()

        tess.draw()
        // COLOR WHEEL


        // COLOR WHEEL CURSOR
        drawCursor(colorWheelCursor, tess, bb)


        // VALUE SEMI CIRCLE
        radius = valueMaxRadius
        GL11.glLineWidth(10.0f)
        bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (i in 0 until vertexCount.toInt()) {
            val angle = i * Math.PI / vertexCount + (Math.PI / 2.0)

            x = centerX + MathHelper.cos(angle.toFloat()) * radius
            y = centerY + MathHelper.sin(angle.toFloat()) * radius

            color = Color.getHSBColor(1f, 0f, (i / vertexCount).toFloat())
            bb.pos(y, x, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        }

        tess.draw()

        drawCursor(valueCursor, tess, bb)

        GlStateManager.popMatrix()
    }

    private fun drawCursor(center: Vec2d, tess: Tessellator, bb: BufferBuilder) {
        val vertexCount = 20.0
        var radius = 1.5
        color = Color.WHITE
        var centerX: Double
        var centerY: Double

        GL11.glLineWidth(2.0f)
        bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (i in 0 until vertexCount.toInt()) {
            val angle = i * 2.0 * Math.PI / vertexCount

            centerX = center.x + MathHelper.cos(angle.toFloat()) * radius
            centerY = center.y + MathHelper.sin(angle.toFloat()) * radius

            bb.pos(centerX, centerY, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        }
        centerX = center.x + MathHelper.cos((2.0 * Math.PI).toFloat()) * radius
        centerY = center.y + MathHelper.sin((2.0 * Math.PI).toFloat()) * radius
        bb.pos(centerX, centerY, 0.0).color(color.red, color.green, color.blue, 255).endVertex()

        tess.draw()

        radius = 2.0
        color = Color.BLACK

        bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)
        for (i in 0 until vertexCount.toInt()) {
            val angle = i * 2.0 * Math.PI / vertexCount

            centerX = center.x + MathHelper.cos(angle.toFloat()) * radius
            centerY = center.y + MathHelper.sin(angle.toFloat()) * radius

            bb.pos(centerX, centerY, 0.0).color(color.red, color.green, color.blue, 255).endVertex()
        }
        centerX = center.x + MathHelper.cos((2.0 * Math.PI).toFloat()) * radius
        centerY = center.y + MathHelper.sin((2.0 * Math.PI).toFloat()) * radius
        bb.pos(centerX, centerY, 0.0).color(color.red, color.green, color.blue, 255).endVertex()

        tess.draw()
    }
}