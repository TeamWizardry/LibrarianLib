package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.RenderableStructure
import com.teamwizardry.librarianlib.features.structure.dynamic.DynamicStructure
import java.awt.Color

class ComponentMaterialsBar(book: IBookGui, id: Int, renderableStructure: RenderableStructure?, dynamicStructure: DynamicStructure?) : ComponentBookMark(book, book.searchIconSprite, id, -8, 1) {

    var focused = false

    init {

        setBookmarkText("Materials", Color.WHITE, -8)

        slideOutShort()

        BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            if (!focused)
                slideOutLong()
        }

        BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            if (!focused)
                slideOutShort()
        }

        BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            focused = if (focused) {
                if (!book.history.empty()) {
                    book.forceInFocus(book.history.pop())
                }
                slideOutShort()
                false
            } else {
                book.placeInFocus(ComponentMaterialList(book, renderableStructure, dynamicStructure))
                slideIn()
                true
            }
        }

    }
}
