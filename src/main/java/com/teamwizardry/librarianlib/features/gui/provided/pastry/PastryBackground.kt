package com.teamwizardry.librarianlib.features.gui.provided.pastry

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.sprite.Sprite

class PastryBackground(type: BackgroundType, posX: Int, posY: Int, width: Int, height: Int):
    GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(BackgroundType.DEFAULT, posX, posY, width, height)

    private val sprite = SpriteLayer(type.sprite, 0, 0, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}