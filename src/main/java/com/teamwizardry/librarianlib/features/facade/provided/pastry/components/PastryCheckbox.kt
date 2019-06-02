package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import kotlin.math.abs

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