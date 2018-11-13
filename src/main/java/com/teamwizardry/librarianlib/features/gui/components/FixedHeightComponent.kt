package com.teamwizardry.librarianlib.features.gui.components

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.value.RMValue
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Vec2d

open class FixedHeightComponent(posX: Int, posY: Int, width: Int, height: Int): GuiComponent(posX, posY, width, height) {
    private val fixedHeight = height
    override var size: Vec2d
        get() = vec(super.size.x, fixedHeight)
        set(value) { super.size = vec(value.x, fixedHeight) }
}