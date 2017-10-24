package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.DrawingUtil
import com.teamwizardry.librarianlib.features.sprite.Sprite
import org.lwjgl.opengl.GL11
import java.awt.Color

open class ComponentSpriteTiled @JvmOverloads constructor(protected var main: Sprite, borderSize: Int, x: Int, y: Int, width: Int = main.width, height: Int = main.height) : GuiComponent(x, y, width, height) {

    var depth = Option<ComponentSpriteTiled, Boolean>(true)
    var color = Option<ComponentSpriteTiled, Color>(Color.WHITE)

    protected var borderSize = 3

    protected var topLeft: Sprite
    protected var topRight: Sprite
    protected var bottomLeft: Sprite
    protected var bottomRight: Sprite
    protected var top: Sprite
    protected var right: Sprite
    protected var bottom: Sprite
    protected var left: Sprite
    protected var middle: Sprite

    init {
        this.borderSize = borderSize

        val insideU = main.width - borderSize
        val insideV = main.height - borderSize

        this.topLeft = main.getSubSprite(0, 0, borderSize, borderSize)
        this.topRight = main.getSubSprite(insideU, 0, borderSize, borderSize)

        this.bottomLeft = main.getSubSprite(0, insideV, borderSize, borderSize)
        this.bottomRight = main.getSubSprite(insideU, insideV, borderSize, borderSize)

        this.top = main.getSubSprite(borderSize, 0, main.width - 2 * borderSize, borderSize)
        this.bottom = main.getSubSprite(borderSize, insideV, main.width - 2 * borderSize, borderSize)

        this.left = main.getSubSprite(0, borderSize, borderSize, main.height - 2 * borderSize)
        this.right = main.getSubSprite(insideU, borderSize, borderSize, main.height - 2 * borderSize)

        this.middle = main.getSubSprite(borderSize, borderSize, main.width - 2 * borderSize, main.height - 2 * borderSize)
    }

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val alwaysTop = !depth.getValue(this)

        if (alwaysTop) {
            // store the current depth function
            GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT)

            // by using GL_ALWAYS instead of disabling depth it writes to the depth buffer
            // imagine a mountain, that is the depth buffer. this causes the sprite to write
            // it's value to the depth buffer, cutting a hole down wherever it's drawn.
            GL11.glDepthFunc(GL11.GL_ALWAYS)
        }
        color.getValue(this).glColor()
        main.tex.bind()
        draw(pos.xf, pos.yf, size.xi, size.yi)

        if (alwaysTop)
            GL11.glPopAttrib()
    }

    fun draw(x: Float, y: Float, width: Int, height: Int) {
        middle.tex.bind()

        DrawingUtil.startDrawingSession()

        val insideX = x + width - borderSize
        val insideY = y + height - borderSize

        topLeft.draw(animationTicks, x, y, borderSize.toFloat(), borderSize.toFloat())

        topRight.draw(animationTicks, insideX, y, borderSize.toFloat(), borderSize.toFloat())

        bottomLeft.draw(animationTicks, x, insideY, borderSize.toFloat(), borderSize.toFloat())
        bottomRight.draw(animationTicks, insideX, insideY, borderSize.toFloat(), borderSize.toFloat())

        left.drawClipped(animationTicks, x, borderSize.toFloat(), borderSize, height - 2 * borderSize)
        right.drawClipped(animationTicks, insideX, borderSize.toFloat(), borderSize, height - 2 * borderSize)

        top.drawClipped(animationTicks, borderSize.toFloat(), 0f, width - 2 * borderSize, borderSize)
        bottom.drawClipped(animationTicks, borderSize.toFloat(), insideY, width - 2 * borderSize, borderSize)

        middle.drawClipped(animationTicks, borderSize.toFloat(), borderSize.toFloat(), width - 2 * borderSize, height - 2 * borderSize)

        DrawingUtil.endDrawingSession()
    }

}
