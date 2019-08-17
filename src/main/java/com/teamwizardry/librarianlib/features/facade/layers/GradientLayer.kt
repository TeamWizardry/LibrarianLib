package com.teamwizardry.librarianlib.features.facade.layers

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.value.RMValue
import com.teamwizardry.librarianlib.features.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.features.kotlin.color
import com.teamwizardry.librarianlib.features.math.Axis2d
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import java.awt.Color

class GradientLayer(val axis: Axis2d, posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    constructor(axis: Axis2d, min: Color, max: Color, posX: Int, posY: Int, width: Int, height: Int)
        : this(axis, posX, posY, width, height) {
        this.addStop(0.0, min)
        this.addStop(1.0, max)
    }
    constructor(axis: Axis2d, min: Color, max: Color, x: Int, y: Int): this(axis, min, max, x, y, 0, 0)
    constructor(axis: Axis2d, min: Color, max: Color): this(axis, min, max, 0, 0, 0, 0)

    val stops = mutableListOf<ColorStop>()

    fun addStop(location: Double, color: Color): ColorStop {
        val stop = ColorStop(location, color)
        stops.add(stop)
        return stop
    }

    override fun draw(partialTicks: Float) {
        stops.sortBy { it.location }
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()

        val tessellator = Tessellator.getInstance()
        val vb = tessellator.buffer

        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()

        GlStateManager.enableBlend()

        if(stops.isNotEmpty()) {
            vb.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)
            if (axis == Axis2d.X) {
                if (stops.first().location != 0.0) {
                    vb.pos(minX, minY, 0.0).color(stops.first().color).endVertex()
                    vb.pos(minX, maxY, 0.0).color(stops.first().color).endVertex()
                }

                stops.forEach { stop ->
                    vb.pos(minX + (maxX-minX)*stop.location, minY, 0.0).color(stop.color).endVertex()
                    vb.pos(minX + (maxX-minX)*stop.location, maxY, 0.0).color(stop.color).endVertex()
                }

                if (stops.last().location != 1.0) {
                    vb.pos(maxX, minY, 0.0).color(stops.last().color).endVertex()
                    vb.pos(maxX, maxY, 0.0).color(stops.last().color).endVertex()
                }
            } else {
                if (stops.first().location != 0.0) {
                    vb.pos(minX, minY, 0.0).color(stops.first().color).endVertex()
                    vb.pos(maxX, minY, 0.0).color(stops.first().color).endVertex()
                }

                stops.forEach { stop ->
                    vb.pos(minX, minY + (maxY-minY)*stop.location, 0.0).color(stop.color).endVertex()
                    vb.pos(maxX, minY + (maxY-minY)*stop.location, 0.0).color(stop.color).endVertex()
                }

                if (stops.last().location != 1.0) {
                    vb.pos(minX, maxY, 0.0).color(stops.last().color).endVertex()
                    vb.pos(maxX, maxY, 0.0).color(stops.last().color).endVertex()
                }
            }
            tessellator.draw()
        }

        GlStateManager.enableTexture2D()
    }

    class ColorStop(location: Double, color: Color) {
        val location_rm: RMValueDouble = RMValueDouble(location)
        val location: Double by location_rm
        val color_rm: RMValue<Color> = RMValue(color)
        var color: Color by color_rm

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ColorStop) return false

            if (location != other.location) return false
            if (color != other.color) return false

            return true
        }

        override fun hashCode(): Int {
            var result = location.hashCode()
            result = 31 * result + color.hashCode()
            return result
        }

        override fun toString(): String {
            return "($color @ $location)"
        }
    }

    override fun debugInfo(): MutableList<String> {
        val list = super.debugInfo()
        list.add("axis = $axis, stops = $stops")
        return list
    }
}