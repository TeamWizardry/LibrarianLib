package com.teamwizardry.librarianlib.facade.pastry.components

import com.teamwizardry.librarianlib.etcetera.eventbus.Event
import com.teamwizardry.librarianlib.etcetera.eventbus.Hook
import com.teamwizardry.librarianlib.facade.layer.GuiLayer
import com.teamwizardry.librarianlib.facade.layer.GuiLayerEvents
import com.teamwizardry.librarianlib.math.Axis2d
import com.teamwizardry.librarianlib.math.Vec2d
import com.teamwizardry.librarianlib.math.clamp
import com.teamwizardry.librarianlib.math.vec
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *
 */
class ScrollPane(x: Int, y: Int, width: Int, height: Int): GuiLayer(x, y, width, height) {
    constructor(x: Int, y: Int): this(x, y, 0, 0)
    constructor(): this(0, 0, 0, 0)
    /**
     * The contents of the scroll pane. If the this component is larger than the pane the pane it will be able to
     * scroll.
     */
    val content: GuiLayer = GuiLayer()

    var useScrollWheel: Boolean = true
    @Suppress("LeakingThis")
    val verticalScrollBar: ScrollBar = ScrollBar(this, Axis2d.Y)
    @Suppress("LeakingThis")
    val horizontalScrollBar: ScrollBar = ScrollBar(this, Axis2d.X)

    private var previousContentSize: Vec2d = vec(Double.NaN, Double.NaN)

    init {
        this.add(content)
        this.dependsOnChildLayout = true
        this.clipToBounds = true
    }

    @Hook
    private fun scroll(e: GuiLayerEvents.MouseScroll) {
        if(useScrollWheel) {
            verticalScrollBar.scrollPosition += e.delta.y
            horizontalScrollBar.scrollPosition += e.delta.x
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

class ScrollBar internal constructor(private val scrollPane: ScrollPane, val axis: Axis2d): GuiLayer() {
    /**
     * The actual handle that will be dragged
     */
    val handle: GuiLayer = GuiLayer()

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
            scrollPane.content.pos = axis.set(scrollPane.content.pos, -value.roundToInt().toDouble())
        }

    private var dragPosition: Double? = null

    init {
        this.add(handle)

        handle.BUS.hook<GuiLayerEvents.MouseDown> {
            if(handle.mouseOver)
                dragPosition = if(axis.get(handle.size) == 0.0) 0.0 else axis.get(handle.mousePos) / axis.get(handle.size)
        }

        handle.BUS.hook<GuiLayerEvents.MouseMove> {
            updateHandle()
        }

        handle.BUS.hook<GuiLayerEvents.MouseUp> {
            updateHandle()
            dragPosition = null
        }
    }

    override fun layoutChildren() {
        val viewportSize = axis.get(scrollPane.size)
        val contentSize = axis.get(scrollPane.content.size)
        val viewportFraction = if(contentSize == .0) 1.0 else min(1.0, viewportSize / contentSize)
        var handleSize = (viewportFraction * axis.get(this.size)).roundToInt().toDouble()
        handleSize = this.BUS.fire(ResizeHandleEvent(handleSize)).size
        handle.size = axis.set(this.size, handleSize.clamp(0.0, axis.get(this.size)))
        updateHandle()
    }

    internal fun updateHandle() {
        val viewportSize = axis.get(scrollPane.size)
        val contentSize = axis.get(scrollPane.content.frame.size)
        val neededScroll = max(0.0, contentSize - viewportSize)

        val handleGap = axis.get(this.size) - axis.get(handle.size)

        if(neededScroll == 0.0) {
            handle.pos = vec(0, 0)
            _scrollPosition = 0.0
        }

        val dragPosition = dragPosition
        if(dragPosition != null) {
            var handlePos = axis.get(mousePos) - axis.get(handle.size) * dragPosition
            handlePos = this.BUS.fire(HandlePosEvent(handlePos)).pos
            handlePos = handlePos.clamp(0.0, handleGap)

            handle.pos = axis.set(vec(0, 0), handlePos)
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

            handle.pos = axis.set(vec(0, 0), handlePos)
        }
    }

    data class ResizeHandleEvent(var size: Double): Event()
    data class HandlePosEvent(var pos: Double): Event()
}
