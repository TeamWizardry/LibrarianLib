package com.teamwizardry.librarianlib.gui.provided.pastry.components.dropdown

import com.teamwizardry.librarianlib.gui.component.GuiLayer
import com.teamwizardry.librarianlib.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.gui.layers.TextLayer
import com.teamwizardry.librarianlib.gui.provided.pastry.PastryTexture
import games.thecodewarrior.bitfont.utils.ExperimentalBitfont

abstract class PastryDropdownItem<T>(
    /**
     * The value associated with this item, or null if the item is a decoration.
     */
    val value: T?,
    /**
     * Whether this item is purely decorative and can't be selected.
     */
    val decoration: Boolean,
    /**
     * Whether this item's layer should be adjusted to fill the dropdown's width
     */
    val listDynamicWidth: Boolean
) {
    /**
     * Creates the layer that will be added to the button and dropdown list. This will be called each time the
     * dropdown opens or a value is selected. The returned layer should already be large enough to fit its contents
     * so the dropdown can be correctly resized and the items correctly spaced.
     */
    abstract fun createLayer(): GuiLayer
}

@ExperimentalBitfont
class DropdownTextItem<T>(value: T, val string: String): PastryDropdownItem<T>(value, false, false) {
    override fun createLayer(): GuiLayer {
        val layer = TextLayer(0, 0, string)
        layer.maxLines = 1
        layer.fitToText()
        return layer
    }
}

class DropdownSeparatorItem<T>: PastryDropdownItem<T>(null, true, true) {
    override fun createLayer(): GuiLayer {
        return SpriteLayer(PastryTexture.dropdownSeparator)
    }
}
