package com.teamwizardry.librarianlib.client.gui.mixin

import com.teamwizardry.librarianlib.client.gui.GuiComponent
import com.teamwizardry.librarianlib.client.util.ScissorUtil
import com.teamwizardry.librarianlib.common.util.math.Vec2d

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
