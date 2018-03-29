package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestClickEvents : GuiBase(100, 100) {
    init {

        val c = ComponentRect(25, 25, 50, 50)
        val text = ComponentText(0, 20)
        val text2 = ComponentText(0, 30)
        c.add(text, text2)
        c.color.setValue(Color(255, 0, 0, 127))
        mainComponents.add(c)

        c.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { event ->
            text.text.setValue("click")
        }
        c.BUS.hook(GuiComponentEvents.MouseClickOutsideEvent::class.java) { event ->
            text.text.setValue("click outside")
        }
        c.BUS.hook(GuiComponentEvents.MouseClickDragInEvent::class.java) { event ->
            text.text.setValue("click drag in")
        }
        c.BUS.hook(GuiComponentEvents.MouseClickDragOutEvent::class.java) { event ->
            text.text.setValue("click drag out")
        }
        c.BUS.hook(GuiComponentEvents.MouseClickAnyEvent::class.java) { event ->
            text2.text.setValue("click")
        }
        c.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            text2.text.setValue("")
        }
    }
}
