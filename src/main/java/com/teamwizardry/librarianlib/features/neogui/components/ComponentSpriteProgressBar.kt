package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.neogui.value.IMValue
import com.teamwizardry.librarianlib.features.neogui.value.IMValueBoolean
import com.teamwizardry.librarianlib.features.neogui.value.IMValueDouble
import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.kotlin.glColor
import com.teamwizardry.librarianlib.features.sprite.ISprite
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

class ComponentSpriteProgressBar @JvmOverloads constructor(
    var sprite: ISprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiComponent(x, y, width, height) {

    class AnimationLoopEvent(val component: ComponentSpriteProgressBar) : Event()
    enum class ProgressDirection { Y_POS, Y_NEG, X_POS, X_NEG }

    val direction_im: IMValue<ProgressDirection> = IMValue(ProgressDirection.Y_POS)
    val progress_im: IMValueDouble = IMValueDouble(1.0)
    val depth_im: IMValueBoolean = IMValueBoolean(true)
    val color_im: IMValue<Color> = IMValue(Color.WHITE)

    var direction: ProgressDirection by direction_im
    var progress: Double by progress_im
    var depth: Boolean by depth_im
    var color: Color by color_im

    var lastAnim: Int = 0

    override fun draw(partialTicks: Float) {
        val alwaysTop = !depth
        val sp = sprite ?: return
        val animationTicks = animator.time.toInt()



        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_ALWAYS)

        if (sp.frameCount > 0 && lastAnim / sp.frameCount < animationTicks / sp.frameCount)
            BUS.fire(AnimationLoopEvent(this))

        lastAnim = animationTicks
        color.glColor()
        sp.bind()

        var w = size.xi
        var h = size.yi
        val dir = direction
        val progress = this.progress

        if (dir == ProgressDirection.Y_POS || dir == ProgressDirection.Y_NEG)
            h = (h * progress).toInt()
        if (dir == ProgressDirection.X_POS || dir == ProgressDirection.X_NEG)
            w = (w * progress).toInt()

        sp.draw(animationTicks, 0f, 0f, w.toFloat(), h.toFloat())
        if (alwaysTop)
            GlStateManager.depthFunc(GL11.GL_LESS)
    }

}
