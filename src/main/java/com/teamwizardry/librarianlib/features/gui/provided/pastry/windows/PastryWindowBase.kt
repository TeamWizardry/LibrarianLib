package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.GuiLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryDropShadowLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Rect2d
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

    open val minSize: Vec2d
        get() = this.size

    open val maxSize: Vec2d
        get() = this.size

    private inner class MoveHandler {
        var edgeDeltas = Vec2d.ZERO
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

            edgeDeltas = vec(deltaX, deltaY)
            clickedEdge = side
        }

        fun endFrameDragOperation() {
            clickedEdge = null
        }

        init {
            window.BUS.hook(GuiComponentEvents.CalculateMousePositionEvent::class.java) { event ->
                val clickedEdge = clickedEdge
                if (clickedEdge != null) {
                    val pointInParent = convertPointToParent(event.mousePos - edgeDeltas)

                    val desiredX = when(clickedEdge.x) {
                        Align2d.X.LEFT -> pointInParent.x
                        Align2d.X.CENTER -> if(clickedEdge == Align2d.CENTER) pointInParent.x else x
                        Align2d.X.RIGHT -> x
                    }
                    val desiredY = when(clickedEdge.y) {
                        Align2d.Y.TOP -> pointInParent.y
                        Align2d.Y.CENTER -> if(clickedEdge == Align2d.CENTER) pointInParent.y else y
                        Align2d.Y.BOTTOM -> y
                    }

                    val desiredWidth = when(clickedEdge.x) {
                        Align2d.X.LEFT -> width + edgeDeltas.x - event.mousePos.x
                        Align2d.X.CENTER -> width
                        Align2d.X.RIGHT -> event.mousePos.x + edgeDeltas.x
                    }
                    val desiredHeight = when(clickedEdge.y) {
                        Align2d.Y.TOP-> height + edgeDeltas.y - event.mousePos.y
                        Align2d.Y.CENTER -> height
                        Align2d.Y.BOTTOM -> event.mousePos.y + edgeDeltas.y
                    }

                    val desiredPos = vec(desiredX, desiredY)
                    val desiredSize = vec(
                        desiredWidth.clamp(minSize.x, maxSize.x),
                        desiredHeight.clamp(minSize.y, maxSize.y)
                    )

                    if (desiredPos != window.pos || desiredSize != window.size) {
                        val frameEvent = window.BUS.fire(
                            WindowFrameWillChangeEvent(window.pos, window.size, desiredPos, desiredSize)
                        )
                        window.pos = frameEvent.newPos
                        window.size = frameEvent.newSize
                        event.mousePos = window.parentComponent?.let { parent ->
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