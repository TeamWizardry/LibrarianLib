package com.teamwizardry.librarianlib.features.neogui.provided.book.structure

import com.teamwizardry.librarianlib.features.neogui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.neogui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.neogui.provided.book.context.ComponentBookMark
import java.awt.Color

class ComponentMaterialsBar(book: IBookGui, id: Int, val materials: StructureMaterials) : ComponentBookMark(book, book.searchIconSprite, id, -8, 1) {

    var focused = false

    init {

        setBookmarkText("Materials", Color.WHITE, -8)

        BUS.hook(GuiComponentEvents.MouseMoveInEvent::class.java) {
            if (!focused)
                slideOutShort()
        }

        BUS.hook(GuiComponentEvents.MouseMoveOutEvent::class.java) {
            if (!focused)
                slideIn()
        }

        BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            val focusedElement = book.focus as? ComponentStructurePage
            if (focusedElement != null) {
                val materialElement = focusedElement.children.firstOrNull { it is ComponentMaterialList }
                if (materialElement != null)
                    focusedElement.remove(materialElement)
                else
                    focusedElement.add(ComponentMaterialList(book, materials, focusedElement))
            }
        }

    }
}
