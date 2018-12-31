package com.teamwizardry.librarianlib.features.gui.provided.pastry.windows

import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.component.supporting.MouseHit
import com.teamwizardry.librarianlib.features.gui.component.supporting.compareTo
import com.teamwizardry.librarianlib.features.gui.components.RootComponent
import com.teamwizardry.librarianlib.features.gui.windows.GuiWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.math.Align2d
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import com.teamwizardry.librarianlib.features.utilities.client.LibCursor
import kotlin.math.abs

open class PastryWindow(width: Int, height: Int): PastryWindowBase(width, height) {
    val header = GuiComponent(0, 0, width, 0)
    val content = GuiComponent(0, 15, width, height)

    private var draggingButton: EnumMouseButton? = null
    private var draggingSide: Align2d? = null

    /**
     * The size of the edge for resizing. This width is on either side of the edge, so a value of 1 means 2 pixels wide
     */
    private var edgeRange = -2.0 .. 1.0
    /**
     * The size of the corner for resizing in taxicab distance
     */
    private var cornerSize: Int = 5

    init {
        header.heighti = 15
        content.clipToBounds = true
        add(header, content)
        addDragHooks(header)
    }

    override var cursor: LibCursor?
        get() = (draggingSide ?: getMouseEdge(mousePos))?.let { frameCursor(it) }
        set(value) {}

    init {
        BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            val edge = getMouseEdge(mousePos)
            if (draggingButton == null && edge != null) {
                draggingButton = event.button
                draggingSide = edge
                beginFrameDragOperation(edge)
                event.cancel()
            }
        }
        BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (draggingButton == event.button && draggingButton != null) {
                draggingButton = null
                draggingSide = null
                endFrameDragOperation()
                event.cancel()
            }
        }
    }

    override fun isPointInBounds(point: Vec2d): Boolean {
        return super.isPointInBounds(point) || draggingButton != null || getMouseEdge(point) != null
    }

    override fun layoutChildren() {
        header.pos = vec(0, 0)
        content.y = header.height
        header.width = width
        content.size = vec(width, size.y - header.height)
        super.layoutChildren()
    }

    /**
     * Pass a component to this method to allow the window to be dragged with it.
     */
    fun addDragHooks(component: GuiComponent) {
        component.BUS.hook(GuiComponentEvents.MouseDownEvent::class.java) { event ->
            if (draggingButton == null && component.mouseOver) {
                draggingButton = event.button
                draggingSide = Align2d.CENTER
                this.beginFrameDragOperation(Align2d.CENTER)
                event.cancel()
            }
        }
        component.BUS.hook(GuiComponentEvents.MouseUpEvent::class.java) { event ->
            if (draggingButton == event.button) {
                draggingButton = null
                draggingSide = null
                this.endFrameDragOperation()
                event.cancel()
            }
        }
    }

    /**
     * Returns the edge or corner the mouse is over, if any
     */
    fun getMouseEdge(pos: Vec2d): Align2d? {
        val dLeft = pos.x
        val dRight = size.x - pos.x
        val dTop = pos.y
        val dBottom = size.y - pos.y

        if(dLeft > 0 && dTop > 0 && dLeft + dTop <= cornerSize) return Align2d.LEFT_TOP
        if(dRight > 0 && dTop > 0 && dRight + dTop <= cornerSize) return Align2d.RIGHT_TOP
        if(dLeft > 0 && dBottom > 0 && dLeft + dBottom <= cornerSize) return Align2d.LEFT_BOTTOM
        if(dRight > 0 && dBottom > 0 && dRight + dBottom <= cornerSize) return Align2d.RIGHT_BOTTOM

        if(dTop > 0 && dBottom > 0 && dLeft in edgeRange) return Align2d.LEFT_CENTER
        if(dTop > 0 && dBottom > 0 && dRight in edgeRange) return Align2d.RIGHT_CENTER
        if(dLeft > 0 && dRight > 0 && dTop in edgeRange) return Align2d.CENTER_TOP
        if(dLeft > 0 && dRight > 0 && dBottom in edgeRange) return Align2d.CENTER_BOTTOM

        return null
    }
}