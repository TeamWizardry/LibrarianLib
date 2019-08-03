package com.teamwizardry.librarianlib.gui.provided.pastry.components

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.Hook
import com.teamwizardry.librarianlib.gui.component.GuiComponent
import com.teamwizardry.librarianlib.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.gui.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *
 */
class ScrollPane(x: Int, y: Int, width: Int, height: Int): GuiComponent(x, y, width, height) {
    constructor(x: Int, y: Int): this(x, y, 0, 0)
    constructor(): this(0, 0, 0, 0)
    /**
     * The contents of the scroll pane. If the this component is larger than the pane the pane it will be able to
     * scroll.
     */
    val content: GuiComponent = GuiComponent()

    var useScrollWheel: Boolean = true
    @Suppress("LeakingThis")
    val verticalScrollBar: ScrollBar = ScrollBar(this, Axis2d.Y)
    @Suppress("LeakingThis")
    val horizontalScrollBar: ScrollBar = ScrollBar(this, Axis2d.X)

    private var previousContentSize: Vec2d = vec(Double.NaN, Double.NaN)

    init {
        this.add(content)
        this.clipToBounds = true
    }

    @Hook
    private fun scroll(e: GuiComponentEvents.MouseWheelEvent) {
        if(useScrollWheel) {
            verticalScrollBar.scrollPosition += e.amount
        }
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
        verticalScrollBar.runLayout()
        verticalScrollBar.updateHandle()
        horizontalScrollBar.runLayout()
        horizontalScrollBar.updateHandle()
    }
}

class ScrollBar internal constructor(private val scrollPane: ScrollPane, val axis: Axis2d): GuiComponent() {
    /**
     * The actual handle that will be dragged
     */
    val handle: GuiComponent = GuiComponent()

    /**
     * The offset for the scroll pane contents. This is stored in actual offset pixels both for convenience and so the
     * scroll position is retained even if the contents change in size. A positive scroll position indicates that the
     * contents should be moved in the negative direction, so this acts like moving the viewport over a static content
     * component. Negative scroll positions are clamped to zero.
     *
     * Setting this value stops any current drag action by the user.
     */
    var scrollPosition: Double
        get() = _scrollPosition
        set(value) {
            _scrollPosition = value
            dragPosition = null
            updateHandle()
        }

    private var _scrollPosition: Double = 0.0
        set(value) {
            field = value
            scrollPane.content.pos = scrollPane.content.pos.setAxis(axis, -value.roundToInt().toDouble())
        }

    private var dragPosition: Double? = null

    init {
        this.add(handle)

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
        val viewportSize = scrollPane.size[axis]
        val contentSize = scrollPane.content.size[axis]
        val viewportFraction = if(contentSize == .0) 1.0 else min(1.0, viewportSize / contentSize)
        var handleSize = (viewportFraction * this.size[axis]).roundToInt().toDouble()
        handleSize = this.BUS.fire(ResizeHandleEvent(handleSize)).size
        handle.size = this.size.setAxis(axis, handleSize.clamp(0.0, this.size[axis]))
        updateHandle()
    }

    internal fun updateHandle() {
        val viewportSize = scrollPane.size[axis]
        val contentSize = scrollPane.content.frame.size[axis]
        val neededScroll = max(0.0, contentSize - viewportSize)

        val handleGap = this.size[axis] - handle.size[axis]

        if(neededScroll == 0.0) {
            handle.pos = vec(0, 0)
            _scrollPosition = 0.0
        }

        val dragPosition = dragPosition
        if(dragPosition != null) {
            var handlePos = mousePos[axis] - handle.size[axis] * dragPosition
            handlePos = this.BUS.fire(HandlePosEvent(handlePos)).pos
            handlePos = handlePos.clamp(0.0, handleGap)

            handle.pos = vec(0, 0).setAxis(axis, handlePos)
            _scrollPosition = neededScroll * if(handleGap == 0.0) 0.0 else handlePos / handleGap
        } else {
            var scrollFraction = if(neededScroll == 0.0) 0.0 else _scrollPosition / neededScroll
            if(scrollFraction !in 0.0 .. 1.0) {
                scrollFraction = scrollFraction.clamp(0.0, 1.0)
                _scrollPosition = neededScroll * scrollFraction
            }

            var handlePos = handleGap * scrollFraction
            handlePos = this.BUS.fire(HandlePosEvent(handlePos)).pos
            handlePos = handlePos.clamp(0.0, handleGap)

            handle.pos = vec(0, 0).setAxis(axis, handlePos)
        }
    }

    data class ResizeHandleEvent(var size: Double): Event()
    data class HandlePosEvent(var pos: Double): Event()
}
