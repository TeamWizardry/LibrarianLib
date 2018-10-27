package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer

class PastryBackground(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private val sprite = SpriteLayer(PastryTexture.background, 0, 0, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}