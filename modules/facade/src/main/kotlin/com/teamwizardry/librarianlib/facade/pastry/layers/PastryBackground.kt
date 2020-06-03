package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.BackgroundTexture

class PastryBackground(type: BackgroundTexture, posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    constructor(posX: Int, posY: Int, width: Int, height: Int): this(BackgroundTexture.DEFAULT, posX, posY, width, height)
    constructor(posX: Int, posY: Int): this(BackgroundTexture.DEFAULT, posX, posY, 0, 0)
    constructor(): this(BackgroundTexture.DEFAULT, 0, 0, 0, 0)
    constructor(type: BackgroundTexture, posX: Int, posY: Int): this(type, posX, posY, 0, 0)
    constructor(type: BackgroundTexture): this(type, 0, 0, 0, 0)

    private val sprite = SpriteLayer(type.background, 0, 0, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}