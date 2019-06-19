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
     * The contents of the scroll pane. If the this component is larger than the pane the pane it will be able to
     * scroll.
     */
    val content: GuiComponent = GuiComponent()

    val verticalScrollBar: PastryScrollBar = PastryScrollBar(this, Axis2d.Y)
    val horizontalScrollBar: PastryScrollBar = PastryScrollBar(this, Axis2d.X)

    internal val viewport: GuiComponent = GuiComponent()
    private val background = PastryBackground(BackgroundTexture.SLIGHT_INSET)

    /**
     * If false the scrollbars will be removed from this component and their layout will not be automatically managed.
     * This is to allow their locations and sizes can be customized.
     */
    var embedScrollbars: Boolean = true
        set(value) {
            field = value
            if(value) {
                if(verticalScrollBar.parent != this) {
                    verticalScrollBar.removeFromParent()
                    this.add(verticalScrollBar)
                }
                if(horizontalScrollBar.parent != this) {
                    horizontalScrollBar.removeFromParent()
                    this.add(horizontalScrollBar)
                }
            } else {
                if(verticalScrollBar.parent == this) {
                    verticalScrollBar.removeFromParent()
                }
                if(horizontalScrollBar.parent == this) {
                    horizontalScrollBar.removeFromParent()
                }
            }
        }

    /**
     * Whether to use the default pastry textures. If set to false all the default pastry sprites will be removed to
     * allow for custom rendering.
     */
    var usePastryTextures: Boolean = true

    private var previousContentSize: Vec2d = vec(Double.NaN, Double.NaN)

    init {
        this.add(background, viewport, verticalScrollBar, horizontalScrollBar)
        viewport.add(content)
        viewport.clipToBounds = true
        background.isVisible_im {
            usePastryTextures
        }
        verticalScrollBar.widthi = 12
        horizontalScrollBar.heighti = 12
    }

    @Hook
    private fun preFrame(e: GuiLayerEvents.PreFrameEvent) {
        val newSize = content.frame.size
        if(newSize != previousContentSize) {
            previousContentSize = newSize
            this.setNeedsLayout()
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()
        if(usePastryTextures)
            viewport.frame = this.bounds.shrink(1.0)
        else
            viewport.frame = this.bounds
        if(embedScrollbars) {
            verticalScrollBar.updateVisibility()
            if (verticalScrollBar.isVisible)
                viewport.frame = viewport.frame.offset(0, 0, -verticalScrollBar.frame.width, 0)

            horizontalScrollBar.updateVisibility()
            if (horizontalScrollBar.isVisible)
                viewport.frame = viewport.frame.offset(0, 0, 0, -horizontalScrollBar.frame.height)


            val viewportFrame = viewport.frame
            verticalScrollBar.frame = rect(viewportFrame.maxX, 0, verticalScrollBar.frame.width, this.height)
            horizontalScrollBar.frame = rect(0, viewportFrame.maxY, this.width, horizontalScrollBar.frame.height)

            verticalScrollBar.runLayout()
            horizontalScrollBar.runLayout()

            verticalScrollBar.updateHandle()
            horizontalScrollBar.updateHandle()
        } else {
            verticalScrollBar.updateVisibility()
            horizontalScrollBar.updateVisibility()
        }
        background.frame = viewport.frame.grow(1.0)
    }
}

class PastryScrollBar internal constructor(private val scrollPane: PastryScrollPane, val axis: Axis2d): GuiComponent() {
    /**
     * The actual handle that will be dragged
     */
    val handle: GuiComponent = GuiComponent()

    /**
     * Whether to show the scrollbar. If this value is null the scrollbar will be automatically shown or hidden based
     * on whether the content is larger than the viewport.
     */
    val scrollbarVisibility: Boolean? = null

    /**
     * Whether to resize the handle based on the scrollable distance. (e.g. if the content is only slightly larger than
     * the viewport the handle will be almost the same height as the entire scrollbar)
     */
    var resizeHandle: Boolean = true

    /**
     * This is used instead of an IMValue callback as the latter can easily be obliterated accidentally.
     */
    internal fun updateVisibility() {
        scrollbarVisibility?.also {
            this.isVisible = it
            return
        }

        val viewportSize = scrollPane.viewport.bounds.size[axis]
        val contentSize = scrollPane.content.frame.size[axis]
        this.isVisible = viewportSize < contentSize
    }

    /**
     * The offset for the scroll pane contents. This is stored in actual offset pixels both for convenience and so the
     * scroll position is retained even if the contents change in size. A positive scroll position indicates that the
     * contents should be moved in the negative direction, so this acts like moving the viewport over a static content
     * component. Negative scroll positions are clamped to zero
     */
    var scrollPosition: Double
        get() = _scrollPosition
        set(value) {
            _scrollPosition = value
            updateHandle()
        }

    private val background = SpriteLayer(PastryTexture.scrollbarTrack)
    private val handleImage = SpriteLayer(
        if(axis == Axis2d.X) PastryTexture.scrollbarHandleHorizontal else PastryTexture.scrollbarHandleVertical
    )

    private var _scrollPosition: Double = 0.0
        set(value) {
            field = value
            scrollPane.content.pos = scrollPane.content.pos.setAxis(axis, -value.roundToInt().toDouble())
        }

    private var dragPosition: Double? = null

    init {
        background.isVisible_im { scrollPane.usePastryTextures }
        handleImage.isVisible_im { scrollPane.usePastryTextures }
        this.add(background, handle)
        handle.add(handleImage)

        handle.BUS.hook<GuiComponentEvents.MouseDownEvent> {
            if(handle.mouseOver)
                dragPosition = if(handle.size[axis] == 0.0) 0.0 else handle.mousePos[axis] / handle.size[axis]
        }

        handle.BUS.hook<GuiComponentEvents.MouseMoveEvent> {
            updateHandle()
        }

        handle.BUS.hook<GuiComponentEvents.MouseUpEvent> {
            updateHandle()
            dragPosition = null
        }
    }

    override fun layoutChildren() {
        background.frame = this.bounds
        val viewportSize = scrollPane.viewport.size[axis]
        val contentSize = scrollPane.content.size[axis]
        var handleSize = handle.size[axis]
        if(resizeHandle) {
            handleSize = (
                (if(contentSize == .0) 1.0 else min(1.0, viewportSize / contentSize)) * this.size[axis]
                ).roundToInt().toDouble()
            if(scrollPane.usePastryTextures) {
                // The pastry handle should always be 3 + 2n
                handleSize = 3.0 + (handleSize - 3).toInt() / 2 * 2
            }
            handleSize = this.BUS.fire(ResizeHandleEvent(handleSize)).size
        }

        if(scrollPane.usePastryTextures) {
            handle.size = (this.size - vec(2, 2)).setAxis(axis, handleSize)
        } else {
            handle.size = this.size.setAxis(axis, handleSize)
        }
        handleImage.frame = handle.bounds
        updateHandle()
    }

    internal fun updateHandle() {
        val viewportSize = scrollPane.viewport.size[axis]
        val contentSize = scrollPane.content.size[axis]
        val neededScroll = max(0.0, contentSize - viewportSize)

        val handleScrollSpace = this.size[axis] - handle.size[axis]

        val pastryInset = if(scrollPane.usePastryTextures) 1.0 else 0.0
        val dragPosition = dragPosition
        if(dragPosition != null) {
            var handlePos = mousePos[axis] - handle.size[axis] * dragPosition
            handlePos = handlePos.clamp(0.0, handleScrollSpace)
            handle.pos = vec(pastryInset, pastryInset).setAxis(axis, handlePos)
            _scrollPosition = neededScroll * if(handleScrollSpace == 0.0) 0.0 else handlePos / handleScrollSpace
        } else {
            var scrollFraction = if(neededScroll == 0.0) 0.0 else scrollPosition / neededScroll
            if(scrollFraction > 1) {
                scrollFraction = 1.0
                _scrollPosition = neededScroll
            }
            handle.pos = vec(pastryInset, pastryInset).setAxis(axis, handleScrollSpace * scrollFraction)
        }
    }

    data class ResizeHandleEvent(var size: Double): Event()
}