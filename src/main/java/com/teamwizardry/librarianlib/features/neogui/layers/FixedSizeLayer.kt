package com.teamwizardry.librarianlib.features.neogui.layers

import com.teamwizardry.librarianlib.features.neogui.component.GuiLayer
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

open class FixedSizeLayer(posX: Int, posY: Int, width: Int, height: Int): GuiLayer(posX, posY, width, height) {
    private val fixedSize = vec(width, height)
    override var size: Vec2d
        get() = fixedSize
        set(value) { /* nop */ }
}
