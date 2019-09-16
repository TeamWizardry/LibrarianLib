package com.teamwizardry.librarianlib.features.facade.mixin

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent

object ScissorMixin {

    @Deprecated("Use ", replaceWith = ReplaceWith("component.clipping.clipToBounds = true"))
    fun scissor(component: GuiComponent) {
        component.clipToBounds = true
    }

}
