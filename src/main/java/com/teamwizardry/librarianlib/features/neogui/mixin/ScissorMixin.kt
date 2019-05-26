package com.teamwizardry.librarianlib.features.neogui.mixin

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponent

object ScissorMixin {

    @Deprecated("Use ", replaceWith = ReplaceWith("component.clipping.clipToBounds = true"))
    fun scissor(component: GuiComponent) {
        component.clipToBounds = true
    }

}
