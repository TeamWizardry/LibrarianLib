package com.teamwizardry.librarianlib.features.gui.provided.book

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.provided.book.ComponentTextField.TextEditEvent
import net.minecraft.client.Minecraft

class ComponentSearchBar(book: IBookGui, id: Int, onType: ((String) -> Unit)?) : ComponentBookMark(book, book.searchIconSprite, id) {

    private val text = ComponentTextField(Minecraft.getMinecraft().fontRenderer,
            2, 1, size.xi - 44 - 2 * book.searchIconSprite.width, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2)

    val isForcedOpen: Boolean
        get() = !text.isFocused && text.text.isEmpty()

    init {

        if (onType != null)
            text.BUS.hook(TextEditEvent::class.java) { onType(it.whole) }
        text.enabledColor = book.book.searchTextColor
        text.selectionColor = book.book.searchTextHighlight
        text.cursorColor = book.book.searchTextCursor
        text.autoFocus = true
        add(text)

        clipping.clipToBounds = true

        BUS.hook(GuiComponentEvents.MouseInEvent::class.java) {
            if (isForcedOpen) {
                slideOutShort()
                text.isVisible = true
            }
        }

        BUS.hook(GuiComponentEvents.MouseOutEvent::class.java) {
            if (isForcedOpen) {
                slideIn()
                text.isVisible = false
            }
        }
    }
}
