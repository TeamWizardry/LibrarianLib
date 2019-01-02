package com.teamwizardry.librarianlib.features.gui.provided.pastry.layers

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus

class PastryBackground(type: BackgroundTexture, posX: Int, posY: Int, width: Int, height: Int):
    GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(BackgroundTexture.DEFAULT, posX, posY, width, height)

    private val sprite = SpriteLayer(type.background, -2, -2, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size + vec(4, 4)
    }
}