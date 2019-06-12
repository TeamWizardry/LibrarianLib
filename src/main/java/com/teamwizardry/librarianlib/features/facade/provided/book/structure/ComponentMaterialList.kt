package com.teamwizardry.librarianlib.features.facade.provided.book.structure

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facade.components.ComponentItemStack
import com.teamwizardry.librarianlib.features.facade.provided.book.IBookGui
import java.awt.Color

class ComponentMaterialList(book: IBookGui, structureMaterials: StructureMaterials, structurePage: ComponentStructurePage) : GuiComponent(0, 0, book.mainBookComponent.size.xi - 32, book.mainBookComponent.size.yi - 32) {

    init {
        structurePage.released = false

        val anchor = GuiComponent(-16, -16)

        val sprite = ComponentSprite(book.paperSprite, 0, 0)
        sprite.translateZ += 800
        sprite.color = Color(1f, 1f, 1f, 0.5f)
        anchor.add(sprite)

        add(anchor)

        for ((index, stack) in structureMaterials.stacks.withIndex()) {
            val stackIcon = ComponentItemStack(index % 7 * 16, index / 7 * 16)
            stackIcon.stack_im { stack[( structurePage.ticks / 80) % stack.size] }
            stackIcon.translateZ += 500
            add(stackIcon)
        }

        BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) {
            if (mouseOver)
                removeFromParent()
        }
    }
}
