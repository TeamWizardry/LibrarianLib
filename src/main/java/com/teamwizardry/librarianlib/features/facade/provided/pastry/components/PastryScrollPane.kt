package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.facade.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.facade.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.facade.provided.pastry.layers.PastryBackground
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *
 */
class PastryScrollPane(x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    constructor(x: Int, y: Int): this(x, y, 0, 0)
    constructor(): this(0, 0, 0, 0)

    /**
     * Whether to show the vertical scroll bar. If this value is null the scroll bar will automatically shown or
     * hidden when needed. Defaults to true
     */
    var showVerticalScrollbar: Boolean? = true

    /**
     * Whether to show the horizontal scroll bar. If this value is null the scroll bar will automatically shown or
     * hidden when needed. Defaults to false
     */
    var showHorizontalScrollbar: Boolean? = false

    private val scrollPane = ScrollPane()

    val content: GuiComponent = scrollPane.content

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

        val dashHeight = ((handle.heighti-4)/3).clamp(0, 5) * 3
        verticalHandleDashes.frame = rect(1, (handle.height - dashHeight) / 2, handle.width - 2, dashHeight)
    }

    private fun layoutHorizontalHandle(e: GuiLayerEvents.LayoutChildren) {
        val handle = scrollPane.horizontalScrollBar.handle
        horizontalHandleBackground.frame = handle.bounds

        val dashWidth = ((handle.widthi-4)/3).clamp(0, 5) * 3
        horizontalHandleDashes.frame = rect((handle.width - dashWidth) / 2, 1, dashWidth, handle.height - 2)
    }

    override fun layoutChildren() {
        super.layoutChildren()

        val contentSize = content.frame.size

        var needsVertical = showVerticalScrollbar ?: (contentSize.y > this.height - 2)
        var needsHorizontal = showHorizontalScrollbar ?: (contentSize.x > this.width - 2)

        if(needsHorizontal)
            needsVertical = contentSize.y > this.height - 2 - scrollBarWidth
        else if(needsVertical)
            needsHorizontal = contentSize.x > this.width - 2 - scrollBarWidth

        val contentAreaSize = vec(
            this.width - if(needsVertical) scrollBarWidth else 0,
            this.height - if(needsHorizontal) scrollBarWidth else 0
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

    companion object {
        @JvmStatic
        val scrollBarWidth = 12
    }
}
