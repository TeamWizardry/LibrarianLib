package com.teamwizardry.librarianlib.features.gui.mixin

import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.utilities.client.ScissorUtil

object ScissorMixin {

    fun <T : GuiComponent<T>> scissor(component: GuiComponent<T>) {
        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) { event ->
            val parent = event.component.parent
            val root = parent?.unTransformRoot(event.component, vec(0, 0)) ?: event.component.pos
            val size = (parent?.unTransformRoot(event.component, event.component.size) ?: (root + event.component.size)) - root
            ScissorUtil.push()
            ScissorUtil.set(root.xi, root.yi, size.xi, size.yi)
            ScissorUtil.enable()
        }
        component.BUS.hook(GuiComponent.PostDrawEvent::class.java) { ScissorUtil.pop() }
    }

}
