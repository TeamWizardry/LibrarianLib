package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.DrawingUtil
import com.teamwizardry.librarianlib.features.sprite.Sprite

class ComponentSpriteCapped(internal var topLeft:

                            Sprite, internal var middle: Sprite, internal var bottomRight: Sprite, internal var horizontal: Boolean, posX: Int, posY: Int, width: Int, height: Int) : GuiComponent(posX, posY, width, height) {

    override fun draw(partialTicks: Float) {
        topLeft.tex.bind()
        DrawingUtil.startDrawingSession()
        val animationTicks = animator.time.toInt()
        if (horizontal) {
            topLeft.draw(animationTicks, 0f, 0f)
            bottomRight.draw(animationTicks, (size.x - bottomRight.width).toFloat(), 0f)
            middle.drawClipped(animationTicks, topLeft.width.toFloat(), 0f, (size.x - (topLeft.width + bottomRight.width)).toInt(), middle.height)
        } else {
            topLeft.draw(animationTicks, 0f, 0f)
            bottomRight.draw(animationTicks, 0f, (size.y - bottomRight.height).toFloat())
            middle.drawClipped(animationTicks, 0f, topLeft.height.toFloat(), middle.height, (size.y - (topLeft.height + bottomRight.height)).toInt())
        }
        DrawingUtil.endDrawingSession()
    }

}
