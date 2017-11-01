package com.teamwizardry.librarianlib.features.gui.component.supporting

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * TODO: Document file ComponentClippingHandler
 *
 * Created by TheCodeWarrior
 */
class ComponentClippingHandler(val component: GuiComponent) {

    /**
     * If true, clip component and its context to within its bounds
     */
    var clipToBounds = false
    /**
     * If nonzero, round the corners of the clipping
     */
    var cornerRadius = 0.0
    /**
     * If nonzero, draw corners with pixels `N` units in size
     */
    var cornerPixelSize = 0

    internal fun pushEnable() {
        val en = Minecraft.getMinecraft().framebuffer.isStencilEnabled
        if(clipToBounds) {
            StencilUtil.push { stencil() }
        }
    }

    internal fun popDisable() {
        if(clipToBounds) {
            StencilUtil.pop { stencil() }
        }
    }

    private fun stencil() {
        GlStateManager.pushAttrib()
        GlStateManager.disableTexture2D()
        GlStateManager.color(1f, 0f, 1f, 0.5f)
        val vb = Tessellator.getInstance().buffer
        val pos = component.pos
        val size = component.size
        val r = cornerRadius

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        vb.pos(0.0   ,        r, 0.0).endVertex()
        vb.pos(0.0   , size.y-r, 0.0).endVertex()
        vb.pos(size.x, size.y-r, 0.0).endVertex()
        vb.pos(size.x,        r, 0.0).endVertex()

        vb.pos(       r, 0.0, 0.0).endVertex()
        vb.pos(       r, r  , 0.0).endVertex()
        vb.pos(size.x-r, r  , 0.0).endVertex()
        vb.pos(size.x-r, 0.0, 0.0).endVertex()

        vb.pos(       r, size.y-r, 0.0).endVertex()
        vb.pos(       r, size.y  , 0.0).endVertex()
        vb.pos(size.x-r, size.y  , 0.0).endVertex()
        vb.pos(size.x-r, size.y-r, 0.0).endVertex()

        Tessellator.getInstance().draw()

        if(cornerRadius > 0) {
            if (cornerPixelSize <= 0) {
                arc(r, r, vec(-r, 0), vec(0, -r))
                arc(size.x - r, size.y - r, vec(r, 0), vec(0, r))
                arc(r, size.y - r, vec(0, r), vec(-r, 0))
                arc(size.x - r, r, vec(0, -r), vec(r, 0))
            } else {
                pixelatedArc(r, r, vec(0, -1), vec(-1, 0))
                pixelatedArc(size.x - r, size.y - r, vec(0, 1), vec(1, 0))
                pixelatedArc(r, size.y - r, vec(-1, 0), vec(0, 1))
                pixelatedArc(size.x - r, r, vec(1, 0), vec(0, -1))
            }
        }
        GlStateManager.popAttrib()
    }

    private fun arc(x: Double, y: Double, vecA: Vec2d, vecB: Vec2d) {
        val vb = Tessellator.getInstance().buffer

        val origin = vec(x, y, 0)

        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)

        vb.pos(origin).endVertex()

        val a = 0
        val d = (Math.PI/2)/16

        (0..16).forEach { i ->
            val angle = a+d*i
            val rad2 = (vecA * Math.sin(angle)) + (vecB * Math.cos(angle))
            val rad3 = vec(rad2.x, rad2.y, 0)
            vb.pos(origin + rad3).endVertex()
        }

        Tessellator.getInstance().draw()
    }

    private fun pixelatedArc(x: Double, y: Double, vecA: Vec2d, vecB: Vec2d) {
        val v = vec(x, y, 0)
        val a3 = vec(vecA.x, vecA.y, 0) * cornerPixelSize
        val b3 = vec(vecB.x, vecB.y, 0) * cornerPixelSize
        val r = cornerRadius / cornerPixelSize
        var x = 0
        var y = r.toInt()
        var d: Int

        val points = mutableMapOf<Int, Int>()
//        (0..15).forEach { i ->
//            points[i] = 15-i
//        }
//        points[0] = 4
//        points[1] = 4
//        points[2] = 3
//        points[3] = 2

        points[x] = y
        d = 3 - 2 * r.toInt()
        while (x <= y) {
            if (d <= 0) {
                d = d + (4 * x + 6)
            } else {
                d = d + 4 * (x - y) + 10
                y--
            }
            x++

            if(x-1 > 0)
                points[x-1] = Math.max(points[x] ?: 0, y)
            if(y-1 > 0)
                points[y-1] = Math.max(points[y] ?: 0, x)
        }

        val vb = Tessellator.getInstance().buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        points.forEach { (y, x) ->
//        y = 0
//        x = 1
            vb.pos(v + a3 * 0 + b3 * y).endVertex()
            vb.pos(v + a3 * x + b3 * y).endVertex()
            vb.pos(v + a3 * x + b3 * (y+1)).endVertex()
            vb.pos(v + a3 * 0 + b3 * (y+1)).endVertex()
        }
        Tessellator.getInstance().draw()

    }
}
