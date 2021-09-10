package com.teamwizardry.librarianlib.facade.pastry.layers.dropdown

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.layers.TextLayer
import com.teamwizardry.librarianlib.facade.layers.text.TextFit
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture

public abstract class PastryDropdownItem<T>(
    /**
     * The value associated with this item, or null if the item is a decoration.
     */
    public val value: T?,
    /**
     * Whether this item is purely decorative and can't be selected.
     */
    public val decoration: Boolean,
    /**
     * Whether this item's layer should be adjusted to fill the dropdown's width
     */
    public val listDynamicWidth: Boolean
) {
    /**
     * Creates the layer that will be added to the button and dropdown list. This will be called each time the
     * dropdown opens or a value is selected. The returned layer should already be large enough to fit its contents
     * so the dropdown can be correctly resized and the items correctly spaced.
     */
    public abstract fun createLayer(): GuiLayer
}

public class DropdownTextItem<T>(value: T, public val string: String): PastryDropdownItem<T>(value, false, false) {
    override fun createLayer(): GuiLayer {
        val layer = TextLayer(0, 0, string)
        layer.maxLines = 1
        layer.fitToText(TextFit.BOTH)
        return layer
    }
}

public class DropdownSeparatorItem<T>: PastryDropdownItem<T>(null, true, true) {
    override fun createLayer(): GuiLayer {
        return SpriteLayer(PastryTexture.dropdownSeparator)
    }
}
