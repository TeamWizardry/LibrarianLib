package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.rendering.SimpleRenderLayers
import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.vertex2d
import com.teamwizardry.librarianlib.facade.value.RMValue
import com.teamwizardry.librarianlib.facade.value.RMValueDouble
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.math.Axis2d
import net.minecraft.client.render.VertexConsumerProvider
import org.lwjgl.opengl.GL11
import java.awt.Color

public class GradientLayer(public val axis: Axis2d, posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    public constructor(axis: Axis2d, min: Color, max: Color, posX: Int, posY: Int, width: Int, height: Int)
        : this(axis, posX, posY, width, height) {
        this.addStop(0.0, min)
        this.addStop(1.0, max)
    }

    public constructor(axis: Axis2d, min: Color, max: Color, x: Int, y: Int): this(axis, min, max, x, y, 0, 0)
    public constructor(axis: Axis2d, min: Color, max: Color): this(axis, min, max, 0, 0, 0, 0)

    public val stops: MutableList<ColorStop> = mutableListOf<ColorStop>()

    public fun addStop(location: Double, color: Color): ColorStop {
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


        if (stops.isNotEmpty()) {
            val buffer = VertexConsumerProvider.immediate(Client.tessellator.buffer)
            val vb = buffer.getBuffer(renderType)

            if (axis == Axis2d.X) {
                if (stops.first().location != 0.0) {
                    vb.vertex2d(context.transform, minX, minY).color(stops.first().color).next()
                    vb.vertex2d(context.transform, minX, maxY).color(stops.first().color).next()
                }

                stops.forEach { stop ->
                    vb.vertex2d(context.transform, minX + (maxX - minX) * stop.location, minY).color(stop.color).next()
                    vb.vertex2d(context.transform, minX + (maxX - minX) * stop.location, maxY).color(stop.color).next()
                }

                if (stops.last().location != 1.0) {
                    vb.vertex2d(context.transform, maxX, minY).color(stops.last().color).next()
                    vb.vertex2d(context.transform, maxX, maxY).color(stops.last().color).next()
                }
            } else {
                if (stops.first().location != 0.0) {
                    vb.vertex2d(context.transform, minX, minY).color(stops.first().color).next()
                    vb.vertex2d(context.transform, maxX, minY).color(stops.first().color).next()
                }

                stops.forEach { stop ->
                    vb.vertex2d(context.transform, minX, minY + (maxY - minY) * stop.location).color(stop.color).next()
                    vb.vertex2d(context.transform, maxX, minY + (maxY - minY) * stop.location).color(stop.color).next()
                }

                if (stops.last().location != 1.0) {
                    vb.vertex2d(context.transform, minX, maxY).color(stops.last().color).next()
                    vb.vertex2d(context.transform, maxX, maxY).color(stops.last().color).next()
                }
            }

            buffer.draw()
        }
    }

    public inner class ColorStop(location: Double, color: Color) {
        public val location_rm: RMValueDouble = rmDouble(location)
        public val location: Double by location_rm
        public val color_rm: RMValue<Color> = rmValue(color)
        public var color: Color by color_rm

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

    private companion object {
        private val renderType = SimpleRenderLayers.flat(GL11.GL_QUADS)
    }
}