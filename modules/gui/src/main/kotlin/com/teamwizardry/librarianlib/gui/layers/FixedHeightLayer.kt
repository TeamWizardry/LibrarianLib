package com.teamwizardry.librarianlib.gui.layers

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

open class FixedHeightLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private val fixedHeight = height
    override var size: Vec2d
        get() = vec(super.size.x, fixedHeight)
        set(value) { super.size = vec(value.x, fixedHeight) }
}