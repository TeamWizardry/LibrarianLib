package com.teamwizardry.librarianlib.math.shapes

import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

import java.util.ArrayList

/**
 * Created by Saad on 15/7/2016.
 */
class BezierCurve2D(
        /**
         * The two points you want to connect with a jagged line
         */
        var startPoint: Vec2d?, var endPoint: Vec2d?) {

    /**
     * The points that will curve the line
     */
    private var startControlPoint: Vec2d? = null
    private var endControlPoint: Vec2d? = null

    // FORMULA: B(t) = (1-t)**3 p0 + 3(1 - t)**2 t P1 + 3(1-t)t**2 P2 + t**3 P3
    val points: ArrayList<Vec2d>
        get() {
            val points = ArrayList<Vec2d>()

            val midpoint = startPoint!!.sub(endPoint).mul(1.0 / 2.0)

            startControlPoint = startPoint!!.sub(midpoint.x, 0.0)
            endControlPoint = endPoint!!.add(midpoint.x, 0.0)
            val pointCount = 50f
            var i = 0f
            while (i < 1) {
                val x = (1 - i).toDouble() * (1 - i).toDouble() * (1 - i).toDouble() * startPoint!!.x + 3.0 * (1 - i).toDouble() * (1 - i).toDouble() * i.toDouble() * startControlPoint!!.x + 3.0 * (1 - i).toDouble() * i.toDouble() * i.toDouble() * endControlPoint!!.x + i.toDouble() * i.toDouble() * i.toDouble() * endPoint!!.x
                val y = (1 - i).toDouble() * (1 - i).toDouble() * (1 - i).toDouble() * startPoint!!.y + 3.0 * (1 - i).toDouble() * (1 - i).toDouble() * i.toDouble() * startControlPoint!!.y + 3.0 * (1 - i).toDouble() * i.toDouble() * i.toDouble() * endControlPoint!!.y + i.toDouble() * i.toDouble() * i.toDouble() * endPoint!!.y
                points.add(Vec2d(x, y))
                i += 1 / pointCount
            }

            return points
        }

    fun draw() {
        GlStateManager.pushMatrix()
        GlStateManager.disableTexture2D()
        GlStateManager.color(0f, 0f, 0f, 1f)

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_LINE_STRIP)
        GL11.glLineWidth(2f)
        GL11.glBegin(GL11.GL_LINE_STRIP)

        for (point in points) GL11.glVertex2d(point.x, point.y)

        GL11.glEnd()
        GL11.glPopMatrix()
        GlStateManager.enableTexture2D()
        GlStateManager.popMatrix()
    }
}