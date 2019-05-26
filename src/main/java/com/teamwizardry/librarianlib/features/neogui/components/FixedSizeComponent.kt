package com.teamwizardry.librarianlib.features.neogui.components

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

open class FixedSizeComponent(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    protected var fixedSize = vec(width, height)
    override var size: Vec2d
        get() = fixedSize
        set(value) { /* nop */ }
}