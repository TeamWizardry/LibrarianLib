package com.teamwizardry.librarianlib.facade.pastry.layers

import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.facade.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.facade.pastry.PastryTexture
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.math.rect
import com.teamwizardry.librarianlib.math.vec

/**
 *
 */
public class PastryScrollPane(x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    public constructor(x: Int, y: Int): this(x, y, 0, 0)
    public constructor(): this(0, 0, 0, 0)

    /**
     * Whether to show the vertical scroll bar. If this value is null the scroll bar will automatically shown or
     * hidden when needed. Defaults to true
     */
    public var showVerticalScrollbar: Boolean? = true

    /**
     * Whether to show the horizontal scroll bar. If this value is null the scroll bar will automatically shown or
     * hidden when needed. Defaults to false
     */
    public var showHorizontalScrollbar: Boolean? = false

    private val scrollPane = ScrollPane()

    public val content: GuiLayer = scrollPane.content

    private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET)
    private val verticalBackground = SpriteLayer(PastryTexture.scrollbarTrack)
    private val horizontalBackground = SpriteLayer(PastryTexture.scrollbarTrack)

    private val verticalHandleBackground = SpriteLayer(PastryTexture.scrollbarHandleVertical)
    private val horizontalHandleBackground = SpriteLayer(PastryTexture.scrollbarHandleHorizontal)
    private val verticalHandleDashes = SpriteLayer(PastryTexture.scrollbarHandleVerticalDashes)
    private val horizontalHandleDashes = SpriteLayer(PastryTexture.scrollbarHandleHorizontalDashes)

    init {
        this.add(background, horizontalBackground, verticalBackground)
        this.add(scrollPane, scrollPane.verticalScrollBar, scrollPane.horizontalScrollBar)

        scrollPane.verticalScrollBar.handle.add(verticalHandleBackground, verticalHandleDashes)
        scrollPane.horizontalScrollBar.handle.add(horizontalHandleBackground, horizontalHandleDashes)
        verticalHandleDashes.anchor = vec(0.5, 0.5)
        horizontalHandleDashes.anchor = vec(0.5, 0.5)

        scrollPane.verticalScrollBar.handle.BUS.hook(::layoutVerticalHandle)
        scrollPane.horizontalScrollBar.handle.BUS.hook(::layoutHorizontalHandle)
        scrollPane.BUS.hook<GuiLayerEvents.LayoutChildren> { layoutChildren() }
    }

    private fun layoutVerticalHandle(e: GuiLayerEvents.LayoutChildren) {
        val handle = scrollPane.verticalScrollBar.handle
        verticalHandleBackground.frame = handle.bounds

        val dashHeight = ((handle.heighti - 4) / 3).clamp(0, 5) * 3
        verticalHandleDashes.frame = rect(1, (handle.height - dashHeight) / 2, handle.width - 2, dashHeight)
    }

    private fun layoutHorizontalHandle(e: GuiLayerEvents.LayoutChildren) {
        val handle = scrollPane.horizontalScrollBar.handle
        horizontalHandleBackground.frame = handle.bounds

        val dashWidth = ((handle.widthi - 4) / 3).clamp(0, 5) * 3
        horizontalHandleDashes.frame = rect((handle.width - dashWidth) / 2, 1, dashWidth, handle.height - 2)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val contentSize = content.frame.size

        var needsVertical = showVerticalScrollbar ?: (contentSize.y > this.height - 2)
        var needsHorizontal = showHorizontalScrollbar ?: (contentSize.x > this.width - 2)

        if (needsHorizontal)
            needsVertical = contentSize.y > this.height - 2 - scrollBarWidth
        else if (needsVertical)
            needsHorizontal = contentSize.x > this.width - 2 - scrollBarWidth

        val contentAreaSize = vec(
            this.width - if (needsVertical) scrollBarWidth else 0,
            this.height - if (needsHorizontal) scrollBarWidth else 0
        )

        background.frame = rect(0, 0, contentAreaSize.x, contentAreaSize.y)
        scrollPane.frame = background.frame.shrink(1.0)

        scrollPane.verticalScrollBar.isVisible = needsVertical
        verticalBackground.isVisible = needsVertical
        scrollPane.horizontalScrollBar.isVisible = needsHorizontal
        horizontalBackground.isVisible = needsHorizontal

        verticalBackground.frame = rect(this.width - scrollBarWidth, 0, scrollBarWidth, contentAreaSize.y)
        scrollPane.verticalScrollBar.frame = verticalBackground.frame.shrink(1.0)
        horizontalBackground.frame = rect(0, this.height - scrollBarWidth, contentAreaSize.x, scrollBarWidth)
        scrollPane.horizontalScrollBar.frame = horizontalBackground.frame.shrink(1.0)
    }

    private companion object {
        //TODO: themes
        @JvmStatic
        private val scrollBarWidth = 12
    }
}
