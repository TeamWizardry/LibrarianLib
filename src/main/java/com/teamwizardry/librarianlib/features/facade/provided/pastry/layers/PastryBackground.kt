package com.teamwizardry.librarianlib.features.facade.provided.pastry.layers

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture

class PastryBackground(type: BackgroundTexture, posX: Int, posY: Int, width: Int, height: Int):
    GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(BackgroundTexture.DEFAULT, posX, posY, width, height)

    private val sprite = SpriteLayer(type.background, 0, 0, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}