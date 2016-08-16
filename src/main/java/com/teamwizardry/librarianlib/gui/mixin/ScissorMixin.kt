package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.util.ScissorUtil

object ScissorMixin {

    fun <T : GuiComponent<T>> scissor(component: GuiComponent<T>) {
        component.BUS.hook(GuiComponent.PreDrawEvent::class.java) { event ->
            val root = event.component.rootPos(Vec2d(0.0, 0.0))
            ScissorUtil.push()
            ScissorUtil.set(root.xi, root.yi, event.component.size.xi, event.component.size.yi)
            ScissorUtil.enable()
        }
        component.BUS.hook(GuiComponent.PostDrawEvent::class.java) { ScissorUtil.pop() }
    }

}
