package com.teamwizardry.librarianlib.gui.components

import com.teamwizardry.librarianlib.core.util.Client
import com.teamwizardry.librarianlib.core.util.SimpleRenderTypes
import com.teamwizardry.librarianlib.core.util.kotlin.color
import com.teamwizardry.librarianlib.core.util.kotlin.pos2d
import com.teamwizardry.librarianlib.gui.value.IMValue
import com.teamwizardry.librarianlib.gui.value.IMValueDouble
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.component.GuiDrawContext
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.vec
import net.minecraft.client.renderer.IRenderTypeBuffer
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class ArcLayer(color: Color, x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    constructor(color: Color, x: Int, y: Int): this(color, x, y, 0, 0)
    constructor(color: Color): this(color, 0, 0, 0, 0)

    init {
        anchor = vec(0.5, 0.5)
    }
    val color_im: IMValue<Color> = IMValue(color)
    var color: Color by color_im

    val startAngle_im: IMValueDouble = IMValueDouble(0.0)
    var startAngle: Double by startAngle_im
    val endAngle_im: IMValueDouble = IMValueDouble(2*PI)
    var endAngle: Double by endAngle_im

    val segmentSize_im: IMValueDouble = IMValueDouble(Math.toRadians(5.0))
    var segmentSize: Double by segmentSize_im

    override fun isPointInBounds(point: Vec2d): Boolean {
        if (point !in bounds)
            return false

        val size = bounds.size
        if(size.x == 0.0 || size.y == 0.0) return false
        // divide by size to adjust for non-square bounds. This puts values on the edges at ±1 on each axis
        val delta = (point/size - vec(0.5, 0.5)) * 2
        val angle = atan2(-delta.x, delta.y) + PI

        val start = startAngle
        var end = endAngle
        if(end > start + PI*2) end = start + PI*2

        return angle in start..end && delta.length() <= 1
    }

    override fun draw(context: GuiDrawContext) {
        val start = startAngle
        var end = endAngle
        if(end > start + PI*2) end = start + PI*2
        val rX = size.x/2
        val rY = size.y/2

        val segmentSize = segmentSize

        val c = color


        val buffer = IRenderTypeBuffer.getImpl(Client.tessellator.buffer)
        val vb = buffer.getBuffer(renderType)

        context.matrix.translate(size.x/2, size.y/2)

        vb.pos2d(context.matrix, 0, 0).color(c).endVertex()

        var a = start
        while(if(start < end) a < end else a > end) {
            val cos = cos(a)
            val sin = sin(a)
            vb.pos2d(context.matrix, rX*sin, rY*-cos).color(c).endVertex()
            if(start < end)
                a += segmentSize
            else
                a -= segmentSize
        }

        val cos = cos(end)
        val sin = sin(end)
        vb.pos2d(context.matrix, rX*sin, rY*-cos).color(c).endVertex()

        buffer.finish()

    }

    companion object {
        private val renderType = SimpleRenderTypes.flat(GL11.GL_TRIANGLE_FAN)
    }
}