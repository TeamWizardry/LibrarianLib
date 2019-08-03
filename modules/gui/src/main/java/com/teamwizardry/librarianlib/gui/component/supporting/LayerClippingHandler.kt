package com.teamwizardry.librarianlib.gui.component.supporting

import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.value.RMValueDouble
import com.teamwizardry.librarianlib.gui.value.RMValueInt
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.kotlin.pos
import com.teamwizardry.librarianlib.features.kotlin.times
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import com.teamwizardry.librarianlib.features.utilities.client.StencilUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

interface ILayerClipping {
    /**
     * If true, clip component and its context to within its bounds. When this is set and both [clippingSprite] and
     * [customClipping] are `null`, mouseover checks will be clipped.
     */
    var clipToBounds: Boolean
    /**
     * If nonzero, round the corners of the clipping
     */
    val cornerRadius_rm: RMValueDouble
    var cornerRadius: Double
    /**
     * If nonzero, draw corners with pixels `N` units in size. Pending implementation this property is ignored when
     * clipping mouseover checks.
     */
    val cornerPixelSize_rm: RMValueInt
    var cornerPixelSize: Int
    /**
     * If nonnull, this sprite is used for clipping. Any pixels that are completely transparent will be masked out.
     * This method of clipping does not support clipping mouseover checks.
     *
     * If [clippingSprite] is nonnull, it will override this sprite.
     */
    var clippingSprite: ISprite?
    /**
     * If nonnull, this function is used for clipping. Any pixels that aren't drawn to will be masked out.
     * This method of clipping does not support clipping mouseover checks.
     *
     * !!WARNING!! You absolutely cannot draw to a pixel twice in this function. If you do everything will break in weird ways.
     *
     * !!WARNING!! This method MUST be able to run twice in a frame without any changes to the rendering. The first time creates the mask, the second time deletes it.
     */
    var customClipping: (() -> Unit)?

    fun isPointClipped(point: Vec2d): Boolean
}

/**
 * TODO: Document file ComponentClippingHandler
 *
 * Created by TheCodeWarrior
 */
class LayerClippingHandler: ILayerClipping {
    lateinit var layer: GuiLayer

    /**
     * If true, clip component and its context to within its bounds. When this is set and both [clippingSprite] and
     * [customClipping] are `null`, mouseover checks will be clipped.
     */
    override var clipToBounds = false
    /**
     * If nonzero, round the corners of the clipping
     */
    override val cornerRadius_rm = RMValueDouble(0.0)
    override var cornerRadius by cornerRadius_rm
    /**
     * If nonzero, draw corners with pixels `N` units in size. Pending implementation this property is ignored when
     * clipping mouseover checks.
     */
    override val cornerPixelSize_rm = RMValueInt(0)
    override var cornerPixelSize by cornerPixelSize_rm

    /**
     * If nonnull, this sprite is used for clipping. Any pixels that are completely transparent will be masked out.
     * This method of clipping does not support clipping mouseover checks.
     *
     * If [clippingSprite] is nonnull, it will override this sprite.
     */
    override var clippingSprite: ISprite? = null
    /**
     * If nonnull, this function is used for clipping. Any pixels that aren't drawn to will be masked out.
     * This method of clipping does not support clipping mouseover checks.
     *
     * !!WARNING!! You absolutely cannot draw to a pixel twice in this function. If you do everything will break in weird ways.
     *
     * !!WARNING!! This method MUST be able to run twice in a frame without any changes to the rendering. The first time creates the mask, the second time deletes it.
     */
    override var customClipping: (() -> Unit)? = null

    internal fun pushEnable() {
        if (clipToBounds || clippingSprite != null || customClipping != null) {
            StencilUtil.push { stencil() }
        }
    }

    internal fun popDisable() {
        if (clipToBounds || clippingSprite != null || customClipping != null) {
            StencilUtil.pop { stencil() }
        }
    }

    private fun stencil() {
        customClipping?.let {
            it()
            GlStateManager.enableTexture2D()
            return

        }

        val sp = clippingSprite
        if(sp != null) {
            GlStateManager.enableTexture2D()
            sp.bind()
            sp.draw(Animator.global.time.toInt(), 0f, 0f, layer.size.xi.toFloat(), layer.size.yi.toFloat())
            return
        }

        GlStateManager.disableTexture2D()
        GlStateManager.color(1f, 0f, 1f, 0.5f)
        val vb = Tessellator.getInstance().buffer
        val size = layer.size
        val r = cornerRadius

        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        vb.pos(0.0, r, 0.0).endVertex()
        vb.pos(0.0, size.y - r, 0.0).endVertex()
        vb.pos(size.x, size.y - r, 0.0).endVertex()
        vb.pos(size.x, r, 0.0).endVertex()

        vb.pos(r, 0.0, 0.0).endVertex()
        vb.pos(r, r, 0.0).endVertex()
        vb.pos(size.x - r, r, 0.0).endVertex()
        vb.pos(size.x - r, 0.0, 0.0).endVertex()

        vb.pos(r, size.y - r, 0.0).endVertex()
        vb.pos(r, size.y, 0.0).endVertex()
        vb.pos(size.x - r, size.y, 0.0).endVertex()
        vb.pos(size.x - r, size.y - r, 0.0).endVertex()

        Tessellator.getInstance().draw()

        if (cornerRadius > 0) {
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
        GlStateManager.enableTexture2D()
    }

    private fun arc(x: Double, y: Double, vecA: Vec2d, vecB: Vec2d) {
        val vb = Tessellator.getInstance().buffer

        val origin = vec(x, y, 0)

        vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION)

        vb.pos(origin).endVertex()

        val a = 0
        val d = (Math.PI / 2) / 16

        (0..16).forEach { i ->
            val angle = a + d * i
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
        var arcX = 0
        var arcY = r.toInt()
        var d: Int

        val points = mutableMapOf<Int, Int>()
//        (0..15).forEach { i ->
//            points[i] = 15-i
//        }
//        points[0] = 4
//        points[1] = 4
//        points[2] = 3
//        points[3] = 2

        points[arcX] = arcY
        d = 3 - 2 * r.toInt()
        while (arcX <= arcY) {
            if (d <= 0) {
                d += (4 * arcX + 6)
            } else {
                d += 4 * (arcX - arcY) + 10
                arcY--
            }
            arcX++

            if (arcX - 1 > 0)
                points[arcX - 1] = Math.max(points[arcX] ?: 0, arcY)
            if (arcY - 1 > 0)
                points[arcY - 1] = Math.max(points[arcY] ?: 0, arcX)
        }

        val vb = Tessellator.getInstance().buffer
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION)

        points.forEach { (y, x) ->
            vb.pos(v + a3 * 0 + b3 * y).endVertex()
            vb.pos(v + a3 * x + b3 * y).endVertex()
            vb.pos(v + a3 * x + b3 * (y + 1)).endVertex()
            vb.pos(v + a3 * 0 + b3 * (y + 1)).endVertex()
        }
        Tessellator.getInstance().draw()
    }

    override fun isPointClipped(point: Vec2d): Boolean {
        if(clippingSprite != null || customClipping != null) return false // we can't clip these
        val point = point + layer.contentsOffset

        if(clipToBounds) {
            val size = layer.size
            if (point.x < 0 || point.x > size.x ||
                    point.y < 0 || point.y > size.y) {
                return true
            }

            if (cornerRadius != 0.0) {
                if (point.x < cornerRadius && point.y < cornerRadius &&
                        point.squareDist(vec(cornerRadius, cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x < cornerRadius && point.y > size.y - cornerRadius &&
                        point.squareDist(vec(cornerRadius, size.y - cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x > size.x - cornerRadius && point.y > size.y - cornerRadius &&
                        point.squareDist(vec(size.x - cornerRadius, size.y - cornerRadius)) > cornerRadius * cornerRadius)
                    return true
                if (point.x > size.x - cornerRadius && point.y < cornerRadius &&
                        point.squareDist(vec(size.x - cornerRadius, cornerRadius)) > cornerRadius * cornerRadius)
                    return true
            }
        }
        return false
    }
}
