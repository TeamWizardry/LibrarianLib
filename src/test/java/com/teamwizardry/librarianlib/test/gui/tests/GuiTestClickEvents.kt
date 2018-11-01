package com.teamwizardry.librarianlib.test.gui.tests

import com.teamwizardry.librarianlib.features.gui.GuiBase
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect
import com.teamwizardry.librarianlib.features.gui.components.ComponentText
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestClickEvents : GuiBase() {
    init {
        main.size = vec(100, 100)

        val c = ComponentRect(25, 25, 50, 50)
        val text = ComponentText(0, 20)
        val text2 = ComponentText(0, 30)
        c.add(text, text2)
        c.color = Color(255, 0, 0, 127)
        main.add(c)

        c.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { event ->
            text.text = "click"
        }
        c.BUS.hook(GuiComponentEvents.MouseClickOutsideEvent::class.java) { event ->
            text.text = "click outside"
        }
        c.BUS.hook(GuiComponentEvents.MouseClickDragInEvent::class.java) { event ->
            text.text = "click drag in"
        }
        c.BUS.hook(GuiComponentEvents.MouseClickDragOutEvent::class.java) { event ->
            text.text = "click drag out"
        }
        c.BUS.hook(GuiComponentEvents.MouseClickAnyEvent::class.java) { event ->
            text2.text = "click"
        }
        c.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            text2.text = ""
        }
    }
}
