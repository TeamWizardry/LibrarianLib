package com.teamwizardry.librarianlib.client.gui.components

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.gui.Option
import com.teamwizardry.librarianlib.client.sprite.Sprite
import com.teamwizardry.librarianlib.client.util.glColor
import com.teamwizardry.librarianlib.common.util.event.Event
import com.teamwizardry.librarianlib.common.util.math.Vec2d
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentSpriteProgressBar @JvmOverloads constructor(var sprite: Sprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiComponent<ComponentSprite>(x, y, width, height) {

    class AnimationLoopEvent(val component: ComponentSpriteProgressBar) : Event()
    enum class ProgressDirection { Y_POS, Y_NEG, X_POS, X_NEG }

    var direction = Option<ComponentSpriteProgressBar, ProgressDirection>(ProgressDirection.Y_POS)
    var progress = Option<ComponentSpriteProgressBar, Float>(1f)
    var depth = Option<ComponentSpriteProgressBar, Boolean>(true)
    var color = Option<ComponentSpriteProgressBar, Color>(Color.WHITE)

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
        if (sp.frameCount > 0 && lastAnim / sp.frameCount < animationTicks / sp.frameCount) {
            BUS.fire(AnimationLoopEvent(this))
        }
        lastAnim = animationTicks
        color.getValue(this).glColor()
        sp.tex.bind()

        var w = size.xi
        var h = size.yi
        val dir = direction.getValue(this)
        val progress = this.progress.getValue(this)

        if (dir == ProgressDirection.Y_POS || dir == ProgressDirection.Y_NEG)
            h = (h * progress).toInt()
        if (dir == ProgressDirection.X_POS || dir == ProgressDirection.X_NEG)
            w = (w * progress).toInt()

        var posX = if (dir == ProgressDirection.X_NEG) (pos.xf + size.xi) - w else pos.xf
        var posY = if (dir == ProgressDirection.Y_NEG) (pos.yf + size.yi) - h else pos.yf

        sp.drawClipped(animationTicks, posX, posY, w, h, dir == ProgressDirection.X_NEG, dir == ProgressDirection.Y_POS)
        if (alwaysTop)
            GL11.glPopAttrib()
    }

}
