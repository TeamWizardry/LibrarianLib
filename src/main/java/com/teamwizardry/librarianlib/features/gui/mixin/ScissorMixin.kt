package com.teamwizardry.librarianlib.features.gui.mixin

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.utilities.client.ScissorUtil

object ScissorMixin {

    fun scissor(component: GuiComponent) {
        component.BUS.hook(GuiComponentEvents.PreDrawEvent::class.java) { event ->
            val reverse = event.component.geometry.thisContextToOtherContext(null)
            val root = reverse * vec(0, 0)
            val size = (reverse * event.component.size) - root
            ScissorUtil.push()
            ScissorUtil.set(root.xi, root.yi, size.xi, size.yi)
            ScissorUtil.enable()
        }
        component.BUS.hook(GuiComponentEvents.PostDrawEvent::class.java) { ScissorUtil.pop() }
    }

}
