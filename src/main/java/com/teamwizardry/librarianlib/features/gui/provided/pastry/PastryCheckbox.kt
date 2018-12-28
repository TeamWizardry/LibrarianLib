package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.component.Hook
import com.teamwizardry.librarianlib.features.gui.components.FixedSizeComponent
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import kotlin.math.abs
import kotlin.math.roundToInt

class PastryCheckbox(posX: Int, posY: Int, radioStyle: Boolean): PastryToggle(posX, posY, 7, 7) {
    constructor(posX: Int, posY: Int): this(posX, posY, false)

    private val sprite = if(radioStyle) PastryTexture.radioButton else PastryTexture.checkbox

    private val background = SpriteLayer(sprite, 0, 0, 7, 7)

    override fun visualStateChanged(visualState: Boolean) {
        val current = background.animationFrame
        if(visualState) {
            background.animationFrame_im.animate(sprite.frameCount-1, abs(sprite.frameCount-current).toFloat())
        } else {
            background.animationFrame_im.animate(0, (current+1).toFloat())
        }
    }

    init {
        this.add(background)
    }
}