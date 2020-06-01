package com.teamwizardry.librarianlib.facade.components

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.value.RMValue
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.facade.component.GuiLayer
import com.teamwizardry.librarianlib.facade.component.GuiDrawContext
import com.teamwizardry.librarianlib.math.Axis2d
import net.minecraft.client.renderer.IRenderTypeBuffer
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

    override fun draw(context: GuiDrawContext) {
        stops.sortBy { it.location }
        val minX = 0.0
        val minY = 0.0
        val maxX = size.xi.toDouble()
        val maxY = size.yi.toDouble()


        if(stops.isNotEmpty()) {
            val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
            val vb = buffer.getBuffer(GradientLayer.renderType)

            if (axis == Axis2d.X) {
                if (stops.first().location != 0.0) {
                    vb.pos2d(context.matrix, minX, minY).color(stops.first().color).endVertex()
                    vb.pos2d(context.matrix, minX, maxY).color(stops.first().color).endVertex()
                }

                stops.forEach { stop ->
                    vb.pos2d(context.matrix, minX + (maxX-minX)*stop.location, minY).color(stop.color).endVertex()
                    vb.pos2d(context.matrix, minX + (maxX-minX)*stop.location, maxY).color(stop.color).endVertex()
                }

                if (stops.last().location != 1.0) {
                    vb.pos2d(context.matrix, maxX, minY).color(stops.last().color).endVertex()
                    vb.pos2d(context.matrix, maxX, maxY).color(stops.last().color).endVertex()
                }
            } else {
                if (stops.first().location != 0.0) {
                    vb.pos2d(context.matrix, minX, minY).color(stops.first().color).endVertex()
                    vb.pos2d(context.matrix, maxX, minY).color(stops.first().color).endVertex()
                }

                stops.forEach { stop ->
                    vb.pos2d(context.matrix, minX, minY + (maxY-minY)*stop.location).color(stop.color).endVertex()
                    vb.pos2d(context.matrix, maxX, minY + (maxY-minY)*stop.location).color(stop.color).endVertex()
                }

                if (stops.last().location != 1.0) {
                    vb.pos2d(context.matrix, minX, maxY).color(stops.last().color).endVertex()
                    vb.pos2d(context.matrix, maxX, maxY).color(stops.last().color).endVertex()
                }
            }

            buffer.finish()
        }

    }

    inner class ColorStop(location: Double, color: Color) {
        val location_rm: RMValueDouble = rmDouble(location)
        val location: Double by location_rm
        val color_rm: RMValue<Color> = rmValue(color)
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

    companion object {
        private val renderType = SimpleRenderTypes.flat(GL11.GL_QUADS)
    }
}