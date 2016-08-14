package com.teamwizardry.librarianlib.gui.mixin

import com.teamwizardry.librarianlib.gui.GuiComponent
import com.teamwizardry.librarianlib.util.ScissorUtil
import com.teamwizardry.librarianlib.math.Vec2d

object ScissorMixin {

    fun scissor(component: GuiComponent<*>) {
        component.preDraw.add({ c, pos, partialTicks ->
            val root = c.rootPos(Vec2d(0.0, 0.0))
            ScissorUtil.push()
            ScissorUtil.set(root.xi, root.yi, c.size.xi, c.size.yi)
            ScissorUtil.enable()
        })
        component.postDraw.addFirst({ c, pos, partialTicks -> ScissorUtil.pop() })
    }

}
