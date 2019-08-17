package com.teamwizardry.librarianlib.features.facade.layers

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer

/**
 * Normally invisible, however in layers with masking enabled, all [MaskLayer] children will be drawn and used to mask
 * their parent.
 */
open class MaskLayer : GuiLayer {
    constructor(): super()
    constructor(posX: Int, posY: Int): super(posX, posY)
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height)
}