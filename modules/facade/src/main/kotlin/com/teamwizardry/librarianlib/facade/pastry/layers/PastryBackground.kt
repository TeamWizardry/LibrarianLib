package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.PastryBackgroundStyle

public class PastryBackground(type: PastryBackgroundStyle, posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    public constructor(posX: Int, posY: Int, width: Int, height: Int): this(PastryBackgroundStyle.DEFAULT, posX, posY, width, height)
    public constructor(posX: Int, posY: Int): this(PastryBackgroundStyle.DEFAULT, posX, posY, 0, 0)
    public constructor(): this(PastryBackgroundStyle.DEFAULT, 0, 0, 0, 0)
    public constructor(type: PastryBackgroundStyle, posX: Int, posY: Int): this(type, posX, posY, 0, 0)
    public constructor(type: PastryBackgroundStyle): this(type, 0, 0, 0, 0)

    private val sprite = SpriteLayer(type.background, 0, 0, 0, 0)

    init {
        this.add(sprite)
    }

    override fun layoutChildren() {
        sprite.size = this.size
    }
}