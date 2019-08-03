package com.teamwizardry.librarianlib.gui.layers

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

open class FixedSizeLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private val fixedSize = vec(width, height)
    override var size: Vec2d
        get() = fixedSize
        set(value) { /* nop */ }
}
