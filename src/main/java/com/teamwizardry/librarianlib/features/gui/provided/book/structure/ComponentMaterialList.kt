package com.teamwizardry.librarianlib.features.gui.provided.book.structure

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import java.awt.Color

class ComponentMaterialList(book: IBookGui, structureMaterials: StructureMaterials, structurePage: ComponentStructurePage) : GuiComponent(0, 0, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32) {

    init {
        structurePage.released = false

        val anchor = ComponentVoid(-16, -16)

        val sprite = ComponentSprite(book.paperSprite, 0, 0)
        sprite.transform.translateZ += 800
        sprite.color.setValue(Color(1f, 1f, 1f, 0.5f))
        anchor.add(sprite)

        add(anchor)

        for ((index, stack) in structureMaterials.stacks.withIndex()) {
            val stackIcon = ComponentStack(index % 7 * 16, index / 7 * 16)
            stackIcon.stack.func { stack[( structurePage.ticks / 80) % stack.size] }
            stackIcon.transform.translateZ += 500
            add(stackIcon)
        }

        BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) {
            if (mouseOver)
                invalidate()
        }
    }
}
