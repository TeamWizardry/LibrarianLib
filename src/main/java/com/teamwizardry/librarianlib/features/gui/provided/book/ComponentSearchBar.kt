package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentTextField.TextEditEvent
import net.minecraft.client.Minecraft

class ComponentSearchBar(book: IBookGui, id: Int, onType: ((String) -> Unit)?) : ComponentBookMark(book, book.searchIconSprite, id, -8, 1) {

    private val textField = ComponentTextField(Minecraft.getMinecraft().fontRenderer,
            2, 1, size.xi - 44 - 2 * book.searchIconSprite.width, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2)

    var focused = false

    init {

        if (focused && onType != null)
            textField.BUS.hook(TextEditEvent::class.java) {
                slideOutLong()
                onType(it.whole)
            }
        textField.enabledColor.setValue(book.book.searchTextColor)
        textField.selectionColor.setValue(book.book.searchTextHighlight)
        textField.cursorColor.setValue(book.book.searchTextCursor)
        textField.autoFocus.setValue(true)
        add(textField)

        clipping.clipToBounds = true

        BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            if (!focused) {
                slideOutShort()
               // text.isVisible = true
            }
        }

        BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            if (!focused) {
                slideIn()
               // text.isVisible = false
            }
        }
        BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            if (!focused) {
                slideOutLong()
                textField.isVisible = true
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
