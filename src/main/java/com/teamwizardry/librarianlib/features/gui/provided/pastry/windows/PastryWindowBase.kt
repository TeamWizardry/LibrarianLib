package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.provided.pastry.layers.PastryDropShadowLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor

open class PastryWindowBase(width: Int, height: Int): GuiWindow(width, height) {
    private val dropShadowLayer = PastryDropShadowLayer(0, 0, width, height, 8)
    var showDropShadow: Boolean
        get() = dropShadowLayer.isVisible
        set(value) { dropShadowLayer.isVisible = value }

    private val moveHandler = MoveHandler()

    init {
        dropShadowLayer.color = PastryTexture.shadowColor
        dropShadowLayer.zIndex = Double.NEGATIVE_INFINITY
        dropShadowLayer.anchor = vec(0.5, 0.5)
        add(dropShadowLayer)
    }

    override fun layoutChildren() {
        super.layoutChildren()
        dropShadowLayer.size = this.size + vec(dropShadowLayer.radius/2.0, 0)
        dropShadowLayer.pos = this.size/2 + vec(0, dropShadowLayer.radius/2.0)
    }

    /**
     * Called when the window is about to be moved or resized
     *
     * @property pos The window's current position
     * @property size The window's current size
     * @property newPos What the window's position will be set to after this event
     * @property newSize What the window's size will be set to after this event
     */
    class WindowFrameWillChangeEvent(val pos: Vec2d, val size: Vec2d, var newPos: Vec2d, var newSize: Vec2d) : Event()

    /**
     * Call this method to start moving or resizing the window along the passed side.
     *
     * Every [side] represents resizing along that edge or corner except for [Align2d.CENTER], which represents
     * dragging the window without resizing. Starting multiple operations is not supported, and beginning a
     * new operation will cancel the current one.
     */
    fun beginFrameDragOperation(side: Align2d) {
        moveHandler.beginFrameDragOperation(side)
    }

    /**
     * Call this method to end the drag operation started with [beginFrameDragOperation]
     */
    fun endFrameDragOperation() {
        moveHandler.endFrameDragOperation()
    }

    val hasFrameDragOperation: Boolean
        get() = moveHandler.clickedEdge != null

    fun frameCursor(side: Align2d): LibCursor? {
        val set = mutableSetOf<Align2d>()
        if(side.x != Align2d.X.CENTER) {
            if(size.x < maxSize.x) set.add(side)
            if(size.x > minSize.x) set.add(side.opposite)
        }
        if(side.y != Align2d.Y.CENTER) {
            if(size.y < maxSize.y) set.add(side)
            if(size.y > minSize.y) set.add(side.opposite)
        }
        return ALIGN_CURSORS[set]
    }

    private var _minSize: Vec2d? = null
    open var minSize: Vec2d
        get() = _minSize ?: this.size
        set(value) { _minSize = value }

    private var _maxSize: Vec2d? = null
    open var maxSize: Vec2d
        get() = _maxSize ?: this.size
        set(value) { _maxSize = value }

    private inner class MoveHandler {
        var edgeDistance = Vec2d.ZERO
        var clickedEdge: Align2d? = null

        val window = this@PastryWindowBase

        fun beginFrameDragOperation(side: Align2d) {
            val deltaX = when(side.x) {
                Align2d.X.LEFT -> mousePos.x
                Align2d.X.CENTER -> mousePos.x
                Align2d.X.RIGHT -> size.x - mousePos.x
            }
            val deltaY = when(side.y) {
                Align2d.Y.TOP -> mousePos.y
                Align2d.Y.CENTER -> mousePos.y
                Align2d.Y.BOTTOM -> size.y - mousePos.y
            }

            edgeDistance = vec(deltaX, deltaY)
            clickedEdge = side
        }

        fun endFrameDragOperation() {
            clickedEdge = null
        }

        init {
            window.BUS.hook(GuiComponentEvents.CalculateMousePositionEvent::class.java) { event ->
                val clickedEdge = clickedEdge
                if (clickedEdge != null) {
                    var newPos = pos
                    var newSize = size

                    if(clickedEdge == Align2d.CENTER) {
                        newPos = convertPointToParent(event.mousePos - edgeDistance)
                    } else {
                        val desiredChange = vec(
                            when(clickedEdge.x) {
                                Align2d.X.LEFT -> edgeDistance.x - event.mousePos.x
                                Align2d.X.CENTER -> 0.0
                                Align2d.X.RIGHT -> edgeDistance.x - (size.x - event.mousePos.x)
                            },
                            when(clickedEdge.y) {
                                Align2d.Y.TOP -> edgeDistance.y - event.mousePos.y
                                Align2d.Y.CENTER -> 0.0
                                Align2d.Y.BOTTOM -> edgeDistance.y - (size.y - event.mousePos.y)
                            }
                        )

                        val allowedChange = vec(
                            (size.x + desiredChange.x).clamp(minSize.x, maxSize.x) - size.x,
                            (size.y + desiredChange.y).clamp(minSize.y, maxSize.y) - size.y
                        )

                        newSize = size + allowedChange
                        newPos = convertPointToParent(vec(
                            if(clickedEdge.x == Align2d.X.LEFT) -allowedChange.x else 0.0,
                            if(clickedEdge.y == Align2d.Y.TOP) -allowedChange.y else 0.0
                        ))
                    }

                    if (newPos != window.pos || newSize != window.size) {
                        val frameEvent = window.BUS.fire(
                            WindowFrameWillChangeEvent(window.pos, window.size, newPos, newSize)
                        )
                        window.pos = frameEvent.newPos
                        window.size = frameEvent.newSize
                        event.mousePos = window.parent?.let { parent ->
                            window.convertPointFromParent(parent.mousePos)
                        } ?: window.mousePos
                    }
                }
            }
        }
    }

    companion object {
        val ALIGN_CURSORS = mapOf(
            setOf(Align2d.RIGHT_TOP) to LibCursor.RESIZE_NE,
            setOf(Align2d.LEFT_TOP) to LibCursor.RESIZE_NW,
            setOf(Align2d.RIGHT_BOTTOM) to LibCursor.RESIZE_SE,
            setOf(Align2d.LEFT_BOTTOM) to LibCursor.RESIZE_SW,
            setOf(Align2d.CENTER_TOP) to LibCursor.RESIZE_N,
            setOf(Align2d.CENTER_BOTTOM) to LibCursor.RESIZE_S,
            setOf(Align2d.RIGHT_CENTER) to LibCursor.RESIZE_E,
            setOf(Align2d.LEFT_CENTER) to LibCursor.RESIZE_W,
            setOf(Align2d.CENTER_TOP, Align2d.CENTER_BOTTOM ) to LibCursor.RESIZE_NS,
            setOf(Align2d.RIGHT_CENTER, Align2d.LEFT_CENTER ) to LibCursor.RESIZE_EW,
            setOf(Align2d.RIGHT_TOP, Align2d.LEFT_BOTTOM ) to LibCursor.RESIZE_NESW,
            setOf(Align2d.LEFT_TOP, Align2d.RIGHT_BOTTOM ) to LibCursor.RESIZE_NWSE
        )
    }
}