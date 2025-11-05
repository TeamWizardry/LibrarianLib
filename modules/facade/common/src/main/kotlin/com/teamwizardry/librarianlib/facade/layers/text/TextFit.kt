package com.teamwizardry.librarianlib.facade.layers.text

public enum class TextFit {
    /**
     * Don't fit the layer's size to the text size at all.
     */
    NONE,
    /**
     * Lay out the text with infinite vertical space, then update the layer height to fit the actual text.
     */
    VERTICAL,
    /**
     * Lay out the text with infinite vertical space, then update the layer width and height to fit the actual text.
     */
    VERTICAL_SHRINK,
    /**
     * Lay out the text with infinite horizontal space, then update the layer width to fit the actual text.
     */
    HORIZONTAL,
    /**
     * Lay out the text with infinite space, then update the layer width and height to fit the actual text.
     */
    BOTH
}
