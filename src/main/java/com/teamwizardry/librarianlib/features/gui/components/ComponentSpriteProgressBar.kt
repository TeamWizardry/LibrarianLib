package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.sprite.ISprite
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentSpriteProgressBar @JvmOverloads constructor(var sprite: ISprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiComponent(x, y, width, height) {

    class AnimationLoopEvent(val component: ComponentSpriteProgressBar) : Event()
    enum class ProgressDirection { Y_POS, Y_NEG, X_POS, X_NEG }

    var direction = Option<ComponentSpriteProgressBar, ProgressDirection>(ProgressDirection.Y_POS)
    var progress = Option<ComponentSpriteProgressBar, Float>(1f)
    var depth = Option<ComponentSpriteProgressBar, Boolean>(true)
    var color = Option<ComponentSpriteProgressBar, Color>(Color.WHITE)

    var lastAnim: Int = 0

    override fun drawComponent(mousePos: Vec2d, partialTicks: Float) {
        val alwaysTop = !depth.getValue(this)
        val sp = sprite ?: return
        val animationTicks = animator.time.toInt()



        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_ALWAYS)

        if (sp.frameCount > 0 && lastAnim / sp.frameCount < animationTicks / sp.frameCount)
            BUS.fire(AnimationLoopEvent(this))

        lastAnim = animationTicks
        color.getValue(this).glColor()
        sp.bind()

        var w = size.xi
        var h = size.yi
        val dir = direction.getValue(this)
        val progress = this.progress.getValue(this)

        if (dir == ProgressDirection.Y_POS || dir == ProgressDirection.Y_NEG)
            h = (h * progress).toInt()
        if (dir == ProgressDirection.X_POS || dir == ProgressDirection.X_NEG)
            w = (w * progress).toInt()

        sp.drawClipped(animationTicks, 0f, 0f, w, h, dir == ProgressDirection.X_NEG, dir == ProgressDirection.Y_NEG)
        
        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_LESS)
    }

}
