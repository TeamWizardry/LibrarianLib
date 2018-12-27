package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.sprite.ISprite

/**
 * Displays a sprite
 *
 * Animated sprites will start their animation the moment the `sprite` property is set to a new array (setting it
 * repeatedly to the same array will not restart the animation)
 */
class SpriteLayer @JvmOverloads constructor(sprite: ISprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiLayer(x, y, width, height) {
    var sprite = sprite
        set(value) {
            if(field != value) restartAnimation()
            field = value
        }

    data class AnimationLoopEvent(val loopCount: Int) : Event()

    /**
     *
     */
    fun restartAnimation() {
        animationStart = animator.time.toInt()
        loopCount = 0
    }

    private var animationStart: Int = 0
    private var loopCount: Int = 0

    override fun draw(partialTicks: Float) {
        val sp = sprite ?: return

        val frame = animator.time.toInt() % sp.frameCount
        if(sp.frameCount > 1) {
            val loopCount = animator.time.toInt() % sp.frameCount
            if (loopCount > this.loopCount) {
                BUS.fire(AnimationLoopEvent(loopCount))
                this.loopCount = loopCount
            }
        }
        sp.bind()
        sp.draw(frame, 0f, 0f, size.xi.toFloat(), size.yi.toFloat())
    }

}
