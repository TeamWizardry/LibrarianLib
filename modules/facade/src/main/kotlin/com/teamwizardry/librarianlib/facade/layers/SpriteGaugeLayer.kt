package com.teamwizardry.librarianlib.facade.components

import com.teamwizardry.librarianlib.math.Cardinal2d
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.mosaic.ISprite
import com.teamwizardry.librarianlib.mosaic.Sprite
import com.teamwizardry.librarianlib.mosaic.WrappedSprite

class SpriteGaugeLayer: LinearGaugeLayer {
    var sprite: Sprite? = null

    constructor(sprite: Sprite?): super() {
        this.sprite = sprite
    }

    constructor(posX: Int, posY: Int, sprite: Sprite?): super(posX, posY) {
        this.sprite = sprite
    }

    constructor(posX: Int, posY: Int, width: Int, height: Int, sprite: Sprite?): super(posX, posY, width, height) {
        this.sprite = sprite
    }

    private val pinnedSprite = object : WrappedSprite() {
        override val wrapped: ISprite?
            get() = sprite
        override val pinTop: Boolean
            get() = direction == Cardinal2d.DOWN
        override val pinBottom: Boolean
            get() = direction == Cardinal2d.UP
        override val pinLeft: Boolean
            get() = direction == Cardinal2d.RIGHT
        override val pinRight: Boolean
            get() = direction == Cardinal2d.LEFT
    }
    private val spriteLayer = SpriteLayer(pinnedSprite)

    init {
        contents.add(spriteLayer)
    }

    override fun update() { // todo yoga
        super.update()
        spriteLayer.frame = contents.bounds
    }
}