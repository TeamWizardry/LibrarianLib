package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.gui.component.GuiComponent

object ScissorMixin {

    @Deprecated("Use ", replaceWith = ReplaceWith("component.clipping.clipToBounds = true"))
    fun scissor(component: GuiComponent) {
        component.clipToBounds = true
    }

}
