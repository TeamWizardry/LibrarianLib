package com.teamwizardry.librarianlib.features.gui.provided.pastry.components.dropdown

import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture

abstract class PastryDropdownItem<T>(
    /**
     * The value associated with this item
     */
    val value: T?,
    /**
     * Whether this item is purely decorative and can't be selected.
     */
    val decoration: Boolean,
    /**
     * Whether this item's list layer can have its width be modified to fit the menu
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
        return SpriteLayer(PastryTexture.dropdownSeparator, 0, 0)
    }
}
