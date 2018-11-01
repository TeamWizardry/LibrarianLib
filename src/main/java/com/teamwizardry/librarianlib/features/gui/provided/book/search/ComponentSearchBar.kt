package com.teamwizardry.librarianlib.features.gui.provided.book.search

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField.FocusEvent
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField.TextEditEvent
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentBookMark
import net.minecraft.client.Minecraft

class ComponentSearchBar(book: IBookGui, id: Int, onType: ((String) -> Unit)?) : ComponentBookMark(book, book.searchIconSprite, id, -8, 1) {

    private val textField = ComponentTextField(Minecraft.getMinecraft().fontRenderer,
            2, 1, size.xi - 44 - 2 * book.searchIconSprite.width, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2)

    init {
        textField.BUS.hook(TextEditEvent::class.java) {
            if (textField.isFocused && it.whole.isEmpty())
                slideOutShort()
            else if (it.whole.isNotEmpty())
                slideOutLong()
            else
                slideIn()

            if (textField.isFocused && onType != null)
                onType(it.whole)
        }

        textField.BUS.hook(FocusEvent::class.java) {
            if (!it.wasFocused) {
                if (textField.text.isEmpty())
                    slideOutShort()
                else
                    slideOutLong()
            } else if (it.wasFocused)
                slideIn()
        }


        textField.enabledColor = book.book.searchTextColor
        textField.selectionColor = book.book.searchTextHighlight
        textField.cursorColor = book.book.searchTextCursor
        textField.autoFocus = true
        add(textField)

        clipToBounds = true

        BUS.hook(GuiComponentEvents.MouseMoveInEvent::class.java) {
            if (!textField.isFocused) {
                if (textField.text.isEmpty())
                    slideOutShort()
                else
                    slideOutLong()
            }
        }

        BUS.hook(GuiComponentEvents.MouseMoveOutEvent::class.java) {
            if (!textField.isFocused)
                slideIn()
        }
        BUS.hook(GuiComponentEvents.MouseClickOutsideEvent::class.java) {
            if (book.context.bookElement is ISearchAlgorithm.ResultAcceptor) {
                val focus = book.focus
                if (focus == null || !focus.mouseOver) {
                    book.up()
                    textField.text = ""
                    textField.isFocused = false
                }
            } else {
                textField.text = ""
                textField.isFocused = false
            }
        }
    }

    override fun slideOutShort() {
        super.slideOutShort()
        textField.isVisible = true
    }

    override fun slideOutLong() {
        super.slideOutLong()
        textField.isVisible = true
    }

    override fun slideIn() {
        super.slideIn()
        textField.isVisible = false
    }
}
