package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.client.sprite.Sprite
import com.teamwizardry.librarianlib.client.util.glColor
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentSprite @JvmOverloads constructor(var sprite: Sprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiComponent<ComponentSprite>(x, y, width, height) {

    class AnimationLoopEvent(val component: ComponentSprite) : Event()


    var depth = Option<ComponentSprite, Boolean>(true)
    var color = Option<ComponentSprite, Color>(Color.WHITE)

    var lastAnim: Int = 0

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val alwaysTop = !depth.getValue(this)
        val sp = sprite
        sp ?: return
        if (alwaysTop) {
            // store the current depth function
            GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT)

            // by using GL_ALWAYS instead of disabling depth it writes to the depth buffer
            // imagine a mountain, that is the depth buffer. this causes the sprite to write
            // it's value to the depth buffer, cutting a hole down wherever it's drawn.
            GL11.glDepthFunc(GL11.GL_ALWAYS)
        }
        if(sp.frameCount > 0 && lastAnim / sp.frameCount < animationTicks / sp.frameCount) {
            BUS.fire(AnimationLoopEvent(this))
        }
        lastAnim = animationTicks
        color.getValue(this).glColor()
        sp.tex.bind()
        sp.draw(animationTicks, pos.xf, pos.yf, size.xi.toFloat(), size.yi.toFloat())
        if (alwaysTop)
            GL11.glPopAttrib()
    }

}
