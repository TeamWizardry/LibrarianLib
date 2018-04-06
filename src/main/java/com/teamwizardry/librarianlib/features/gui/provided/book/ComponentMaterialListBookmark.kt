package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import net.minecraft.client.Minecraft

class ComponentMaterialListBookmark(book: IBookGui, id: Int, list: ComponentMaterialList) : ComponentBookMark(book, book.searchIconSprite, id, -8, 1) {

    var focused = false

    init {
        clipping.clipToBounds = true

        BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            if (!focused) {
                slideOutShort()
            }
        }

        BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            if (!focused) {
                slideIn()
            }
        }
        BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (!focused) {
                slideOutLong()

                val currentElement = book.currentElement
                if (currentElement != null)
                    book.history.push(currentElement)

                book.focus?.invalidate()
                book.focus = list
                book.focus?.let { book.mainBookComponent.add(it) }

                focused = true
            } else {
                if (!book.history.empty()) {
                    book.forceInFocus(book.history.pop())
                }
                focused = false
                slideIn()
            }
        }
    }
}
