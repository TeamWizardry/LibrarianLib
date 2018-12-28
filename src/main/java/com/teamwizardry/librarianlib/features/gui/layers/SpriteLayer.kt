package com.teamwizardry.librarianlib.features.gui.layers

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.value.IMValue
import com.teamwizardry.librarianlib.features.gui.value.IMValueInt
import com.teamwizardry.librarianlib.features.sprite.ISprite

/**
 * Displays a sprite
 *
 * Animated sprites will start their animation the moment the `sprite` property is set to a new array (setting it
 * repeatedly to the same array will not restart the animation)
 */
class SpriteLayer @JvmOverloads constructor(var sprite: ISprite?, x: Int, y: Int, width: Int = sprite?.width ?: 16, height: Int = sprite?.height ?: 16) : GuiLayer(x, y, width, height) {

    var animationFrame_im: IMValueInt = IMValueInt(0)
    var animationFrame: Int by animationFrame_im

    override fun draw(partialTicks: Float) {
        val sp = sprite ?: return

        sp.bind()
        sp.draw(animationFrame % sp.frameCount, 0f, 0f, size.xi.toFloat(), size.yi.toFloat())
    }

}
