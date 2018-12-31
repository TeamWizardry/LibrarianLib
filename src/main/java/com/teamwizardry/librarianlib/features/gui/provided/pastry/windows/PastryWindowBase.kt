package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryDropShadowLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.plus

open class PastryWindowBase(width: Int, height: Int): GuiWindow(width, height) {
    private val dropShadowLayer = PastryDropShadowLayer(0, 0, width, height, 8)
    var showDropShadow: Boolean
        get() = dropShadowLayer.isVisible
        set(value) { dropShadowLayer.isVisible = value }

    init {
        dropShadowLayer.color = PastryTexture.shadowColor
        dropShadowLayer.zIndex = Double.NEGATIVE_INFINITY
        dropShadowLayer.anchor = vec(0.5, 0.5)
        add(dropShadowLayer)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        dropShadowLayer.size = this.size + vec(dropShadowLayer.radius/2.0, 0)
        dropShadowLayer.pos = this.size/2 + vec(0, dropShadowLayer.radius/2.0)
    }
}