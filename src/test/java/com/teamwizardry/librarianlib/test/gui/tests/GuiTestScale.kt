package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.GuiComponent
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestScale : GuiBase(100, 100) {
    init {

        val p = ComponentVoid(0,0)
        p.childScale = 2.0
        p.BUS.hook(GuiComponent.MouseClickEvent::class.java) {
            p.childScale = 1/p.childScale
            p.childTranslation += vec(2, 2)
        }
        val c = ComponentRect(-10, -10, 50, 50)
        c.color.setValue(Color.RED)
        val bg = ComponentRect(25, 25, 50, 50)
        bg.color.setValue(Color.GREEN)
        val scissor = ComponentVoid(5, 5, 30, 30)
        ScissorMixin.scissor(scissor)
        scissor.add(c)
        bg.add(scissor)
        p.add(bg)
        mainComponents.add(p)

    }
}
