package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.DrawingUtil
import com.teamwizardry.librarianlib.features.sprite.Sprite

class ComponentSpriteCapped(internal var topLeft:

                            Sprite, internal var middle: Sprite, internal var bottomRight: Sprite, internal var horizontal: Boolean, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        topLeft.tex.bind()
        DrawingUtil.startDrawingSession()
        if (horizontal) {
            topLeft.draw(animationTicks, pos.xf + 0, pos.yf + 0)
            bottomRight.draw(animationTicks, pos.xf + (size.x - bottomRight.width).toFloat(), pos.yf + 0)
            middle.drawClipped(animationTicks, pos.xf + topLeft.width, pos.yf + 0, (size.x - (topLeft.width + bottomRight.width)).toInt(), middle.height)
        } else {
            topLeft.draw(animationTicks, pos.xf + 0, pos.yf + 0)
            bottomRight.draw(animationTicks, pos.xf + 0, pos.yf + (size.y - bottomRight.height).toFloat())
            middle.drawClipped(animationTicks, pos.xf + 0, pos.yf + topLeft.height, middle.height, (size.y - (topLeft.height + bottomRight.height)).toInt())
        }
        DrawingUtil.endDrawingSession()
    }

}
