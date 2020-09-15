package com.teamwizardry.librarianlib.facade.layers

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.facade.value.IMValue
import com.teamwizardry.librarianlib.facade.value.IMValueDouble
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiDrawContext
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.renderer.IRenderTypeBuffer
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

public class ArcLayer(color: Color, x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    public constructor(color: Color, x: Int, y: Int): this(color, x, y, 0, 0)
    public constructor(color: Color): this(color, 0, 0, 0, 0)

    public val color_im: IMValue<Color> = imValue(color)
    public var color: Color by color_im

    /**
     * The clockwise start angle in radians
     */
    public val startAngle_im: IMValueDouble = imDouble(0.0)

    /**
     * The clockwise start angle in radians
     */
    public var startAngle: Double by startAngle_im

    /**
     * The clockwise end angle in radians
     */
    public val endAngle_im: IMValueDouble = imDouble(2 * PI)

    /**
     * The clockwise end angle in radians
     */
    public var endAngle: Double by endAngle_im

    public val segmentSize_im: IMValueDouble = imDouble(Math.toRadians(5.0))
    public var segmentSize: Double by segmentSize_im

    override fun isPointInBounds(point: Vec2d): Boolean {
        if (point !in bounds)
            return false

        val size = bounds.size
        if (size.x == 0.0 || size.y == 0.0) return false
        // divide by size to adjust for non-square bounds. This puts values on the edges at ±1 on each axis
        val delta = (point / size - vec(0.5, 0.5)) * 2
        val angle = atan2(-delta.x, delta.y) + PI

        val start = startAngle
        var end = endAngle
        if (end > start + PI * 2) end = start + PI * 2

        return angle in start..end && delta.length() <= 1
    }

    override fun draw(context: GuiDrawContext) {
        val start = min(startAngle, endAngle)
        var end = max(startAngle, endAngle)
        if (end > start + PI * 2) end = start + PI * 2
        if (start == end) return
        val rX = size.x / 2
        val rY = size.y / 2

        val segmentSize = segmentSize

        val c = color

        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        context.matrix.translate(size.x / 2, size.y / 2)

        vb.pos2d(context.matrix, 0, 0).color(c).endVertex()

        // we go from end to start because while the angles are measured clockwise, we need the vertices to be in
        // counterclockwise order
        var a = end
        while (a > start) {
            val cos = cos(a)
            val sin = sin(a)
            vb.pos2d(context.matrix, rX * sin, rY * -cos).color(c).endVertex()
            a -= segmentSize
        }

        if (a != start) {
            val cos = cos(start)
            val sin = sin(start)
            vb.pos2d(context.matrix, rX * sin, rY * -cos).color(c).endVertex()
        }

        buffer.finish()
    }

    private companion object {
        private val renderType = SimpleRenderTypes.flat(GL11.GL_TRIANGLE_FAN)
    }
}